package de.plixo.galactic.typed.lowering;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.types.Class;

import static de.plixo.galactic.exception.FlairKind.NAME;

/**
 * The Symbols stage is responsible for resolving symbols and names. It's the first stage of the Expression parser.
 */
public class Symbols implements Tree<Context, Integer> {


    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new FlairException(STR."Expression of type \{expression.getClass()
                .getSimpleName()} not implemented for Symbol stage");
    }

    @Override
    public Expression parseWhileExpression(WhileExpression whileExpression, Context context,
                                           Integer hint) {
        var condition = parse(whileExpression.condition(), context, hint);
        var body = parse(whileExpression.body(), context, hint);
        return new WhileExpression(whileExpression.region(), condition, body);
    }

    @Override
    public Expression parseThisExpression(ThisExpression thisExpression, Context context,
                                          Integer unused) {
        return thisExpression;
    }

    @Override
    public Expression parseAssign(AssignExpression expression, Context context, Integer unused) {
        var left = parse(expression.left(), context, 0);
        var value = parse(expression.right(), context, 0);
        if (left instanceof VarExpression varExpression) {
            return new LocalVariableAssign(expression.region(), varExpression.variable(), value);
        } else {
            if (left instanceof StaticFieldExpression staticFieldExpression) {
                return new PutStaticFieldExpression(expression.region(),
                        staticFieldExpression.field(), value);
            }
            return new AssignExpression(expression.region(), left, value);
        }
    }

    @Override
    public Expression parseConstructExpression(ConstructExpression expression, Context context,
                                               Integer unused) {

        var parsed = expression.arguments().stream().map(ref -> {
            context.pushScope();
            var parse = parse(ref, context, 0);
            context.popScope();
            return parse;
        }).toList();

        var type = expression.getType(context);
        if (type instanceof Class aClass) {
            return new StellaClassConstructExpression(expression.region(), aClass, parsed);
        } else {
            throw new NullPointerException(STR."not supported yet from class \{type}");
        }
    }

    @Override
    public Expression parseStringExpression(StringExpression expression, Context context,
                                            Integer unused) {
        return expression;
    }

    @Override
    public Expression parseNumberExpression(NumberExpression expression, Context context,
                                            Integer unused) {
        return expression;
    }


    @Override
    public Expression parseBooleanExpression(BooleanExpression expression, Context context,
                                             Integer unused) {
        return expression;
    }

    @Override
    public Expression parseCastExpression(CastExpression expression, Context context,
                                          Integer unused) {
        var parsed = parse(expression.object(), context, 0);
        return new CastExpression(expression.region(), parsed, expression.type());
    }

    @Override
    public Expression parseCastCheckExpression(CastCheckExpression expression, Context context,
                                               Integer unused) {
        var parsed = parse(expression.object(), context, 0);
        return new CastCheckExpression(expression.region(), parsed, expression.type());
    }

    @Override
    public Expression parseDotNotation(DotNotation expression, Context context, Integer unused) {
        context.pushScope();
        var parsed = parse(expression.object(), context, 0);
        context.popScope();

        var id = expression.id();
        var region = expression.region();
        return switch (parsed) {
            case UnitExpression unitExpression -> {
                var unit = unitExpression.unit();
                var dotNotation = unit.getDotNotation(region, id);
                if (dotNotation == null) {
                    throw new FlairCheckException(region, NAME,
                            STR."Symbol \{id} not found in unit \{unit.name()}");
                }
                yield dotNotation;
            }
            case StellaPackageExpression packageExpression -> {
                var thePackage = packageExpression.thePackage();
                var dotNotation = thePackage.getDotNotation(region, id);
                if (dotNotation == null) {
                    throw new FlairCheckException(region, NAME,
                            STR."Symbol \{id} not found in package \{thePackage.name()}");
                }
                yield dotNotation;
            }
            case StaticClassExpression staticClassExpression -> {
                var stellaClass = staticClassExpression.theClass();
                yield stellaClass.getStaticDotNotation(region, id, context);
            }
            default -> new DotNotation(region, parsed, id);
        };
    }


    @Override
    public Expression parseCallNotation(CallNotation expression, Context context, Integer unused) {
        context.pushScope();
        var parsed = parse(expression.object(), context, 0);
        context.popScope();

        var arguments = expression.arguments().stream().map(ref -> {
            context.pushScope();
            var parse = parse(ref, context, 0);
            context.popScope();
            return parse;
        }).toList();
        return new CallNotation(expression.region(), parsed, arguments);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, Context context,
                                            Integer unused) {
        context.pushScope();
        var parsedCondition = parse(expression.condition(), context, 0);
        context.popScope();


        context.pushScope(new Scope(context.scope()));
        var parsedThen = parse(expression.then(), context, 0);
        context.popScope();
        Expression parsedElse = null;
        if (expression.elseExpression() != null) {
            context.pushScope(new Scope(context.scope()));
            parsedElse = parse(expression.elseExpression(), context, 0);
            context.popScope();
        }
        return new BranchExpression(expression.region(), parsedCondition, parsedThen, parsedElse);
    }


    @Override
    public Expression parseBlockExpression(BlockExpression blockExpression, Context context,
                                           Integer unused) {
        context.pushScope();
        var parsed =
                blockExpression.expressions().stream().map(ref -> parse(ref, context, 0)).toList();
        context.popScope();
        return new BlockExpression(blockExpression.region(), parsed);
    }

    @Override
    public Expression parseVarDefExpression(VarDefExpression varDefExpression, Context context,
                                            Integer unused) {
        var region = varDefExpression.region();
        var scope = context.scope();
        var name = varDefExpression.name();
        var existingVariable = scope.getVariable(name);
        if (existingVariable != null) {
            throw new FlairCheckException(varDefExpression.region(), NAME,
                    STR."Variable \{name} already exists");
        }
        if (!isValidVariableName(name, context)) {
            throw new FlairCheckException(varDefExpression.region(), NAME,
                    STR."Variable \{name} is not a valid name");
        }
        var variable = new Scope.Variable(name, 0, null);
        scope.addVariable(variable);
        context.pushScope();
        var parsed = parse(varDefExpression.expression(), context, 0);
        context.popScope();
        return new VarDefExpression(region, name, varDefExpression.hint(), parsed, variable);
    }

    @Override
    public Expression parseSymbolExpression(SymbolExpression expression, Context context,
                                            Integer unused) {
        var id = expression.id();
        var region = expression.region();
        if (id.equals("true") || id.equals("false")) {
            return new BooleanExpression(region, Boolean.parseBoolean(id));
        }
        var symbolExpression = context.getSymbolExpression(region, id, context);
        if (symbolExpression != null) {
            return symbolExpression;
        }
        throw new FlairCheckException(region, NAME, STR."Symbol '\{id}' not found");
    }

    private static boolean isValidVariableName(String name, Context context) {
        return !name.equals("true") && !name.equals("false");
    }
}
