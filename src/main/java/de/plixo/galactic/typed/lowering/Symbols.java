package de.plixo.galactic.typed.lowering;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.types.Class;

import static de.plixo.galactic.exception.FlairKind.NAME;

public class Symbols implements Tree<Context> {


    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new FlairException(STR."Expression of type \{expression.getClass()
                .getSimpleName()} not implemented for Symbol stage");
    }

    @Override
    public Expression parseAssign(AssignExpression expression, Context context) {
        var left = parse(expression.left(), context);
        var value = parse(expression.right(), context);
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
    public Expression parseConstructExpression(ConstructExpression expression, Context context) {

        var parsed = expression.arguments().stream().map(ref -> {
            context.pushScope();
            var parse = parse(ref, context);
            context.popScope();
            return parse;
        }).toList();

        var type = expression.getType(context);
        if (type instanceof Class aClass) {
            return new StellaClassConstructExpression(expression.region(), aClass, parsed);
        } else {
            throw new NullPointerException("not supported yet from class " + type);
        }
    }

    @Override
    public Expression parseStringExpression(StringExpression expression, Context context) {
        return expression;
    }

    @Override
    public Expression parseNumberExpression(NumberExpression expression, Context context) {
        return expression;
    }


    @Override
    public Expression parseBooleanExpression(BooleanExpression expression, Context context) {
        return expression;
    }
    @Override
    public Expression parseCastExpression(CastExpression expression, Context context) {
        var parsed = parse(expression.object(), context);
        return new CastExpression(expression.region(), parsed, expression.type());
    }

    @Override
    public Expression parseCastCheckExpression(CastCheckExpression expression, Context context) {
        var parsed = parse(expression.object(), context);
        return new CastCheckExpression(expression.region(), parsed, expression.type());
    }

    @Override
    public Expression parseDotNotation(DotNotation expression, Context context) {
        context.pushScope();
        var parsed = parse(expression.object(), context);
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
    public Expression parseCallNotation(CallNotation expression, Context context) {
        context.pushScope();
        var parsed = parse(expression.object(), context);
        context.popScope();

        var arguments = expression.arguments().stream().map(ref -> {
            context.pushScope();
            var parse = parse(ref, context);
            context.popScope();
            return parse;
        }).toList();
        return new CallNotation(expression.region(), parsed, arguments);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, Context context) {
        context.pushScope();
        var parsedCondition = parse(expression.condition(), context);
        context.popScope();


        context.pushScope(new Scope(context.scope()));
        var parsedThen = parse(expression.then(), context);
        context.popScope();
        Expression parsedElse = null;
        if (expression.elseExpression() != null) {
            context.pushScope(new Scope(context.scope()));
            parsedElse = parse(expression.elseExpression(), context);
            context.popScope();
        }
        return new BranchExpression(expression.region(), parsedCondition, parsedThen, parsedElse);
    }


    @Override
    public Expression parseBlockExpression(BlockExpression blockExpression, Context context) {
        context.pushScope();
        var parsed =
                blockExpression.expressions().stream().map(ref -> parse(ref, context)).toList();
        context.popScope();
        return new BlockExpression(blockExpression.region(), parsed);
    }

    @Override
    public Expression parseVarDefExpression(VarDefExpression varDefExpression, Context context) {
        var region = varDefExpression.region();
        var scope = context.scope();
        var name = varDefExpression.name();
        var existingVariable = scope.getVariable(name);
        if (existingVariable != null) {
            throw new FlairCheckException(varDefExpression.region(), NAME,
                    "Variable " + name + " already exists");
        }
        if (!isValidVariableName(name, context)) {
            throw new FlairCheckException(varDefExpression.region(), NAME,
                    "Variable " + name + " is not a valid name");
        }
        var variable = new Scope.Variable(name, 0, null, null);
        scope.addVariable(variable);
        context.pushScope();
        var parsed = parse(varDefExpression.expression(), context);
        context.popScope();
        return new VarDefExpression(region, name, varDefExpression.hint(), parsed, variable);
    }

    @Override
    public Expression parseSymbolExpression(SymbolExpression expression, Context context) {
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
