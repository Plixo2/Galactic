package de.plixo.galactic.tir.stages;

import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.Scope;
import de.plixo.galactic.tir.expressions.*;
import de.plixo.galactic.types.Class;

import java.util.Objects;

public class Symbols implements Tree<Context> {


    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new NullPointerException(
                "Expression of type " + expression.getClass().getSimpleName() +
                        " not implemented for Symbol stage");
    }

    @Override
    public Expression parseAssign(AssignExpression expression, Context context) {
        var left = parse(expression.left(), context);
        var value = parse(expression.right(), context);
        if (left instanceof VarExpression varExpression) {
            return new LocalVariableAssign(varExpression.variable(), value);
        } else {
            throw new NullPointerException("not supported yet");
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
            return new StellaClassConstructExpression(aClass, parsed);
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
    public Expression parseDotNotation(DotNotation expression, Context context) {
        context.pushScope();
        var parsed = parse(expression.object(), context);
        context.popScope();

        var id = expression.id();
        return switch (parsed) {
            case UnitExpression unitExpression -> {
                var unit = unitExpression.unit();
                yield Objects.requireNonNull(unit.getDotNotation(id),
                        "Symbol " + id + " not found in unit " + unit.name());
            }
            case StellaPackageExpression packageExpression -> {
                var thePackage = packageExpression.thePackage();
                yield Objects.requireNonNull(thePackage.getDotNotation(id),
                        "Symbol " + id + " not found in package " + thePackage.name());
            }
            case StaticClassExpression staticClassExpression -> {
                var stellaClass = staticClassExpression.theClass();
                yield Objects.requireNonNull(stellaClass.getStaticDotNotation(id, context),
                        "Symbol " + id + " not found in class " + stellaClass.name());
            }
            default -> new DotNotation(parsed, id);
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
        return new CallNotation(parsed, arguments);
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
        return new BranchExpression(parsedCondition, parsedThen, parsedElse);
    }


    @Override
    public Expression parseBlockExpression(BlockExpression blockExpression, Context context) {
        context.pushScope();
        var parsed =
                blockExpression.expressions().stream().map(ref -> parse(ref, context)).toList();
        context.popScope();
        return new BlockExpression(parsed);
    }

    @Override
    public Expression parseVarDefExpression(VarDefExpression varDefExpression, Context context) {
        var scope = context.scope();
        var name = varDefExpression.name();
        var existingVariable = scope.getVariable(name);
        if (existingVariable != null) {
            throw new NullPointerException("Variable " + name + " already exists");
        }
        if (!isValidVariableName(name, context)) {
            throw new NullPointerException("Variable " + name + " is not a valid name");
        }
        var variable = new Scope.Variable(name, 0, null, null);
        scope.addVariable(variable);
        context.pushScope();
        var parsed = parse(varDefExpression.expression(), context);
        context.popScope();
        return new VarDefExpression(name, varDefExpression.hint(), parsed, variable);
    }

    @Override
    public Expression parseSymbolExpression(SymbolExpression expression, Context context) {
        var id = expression.id();
        if (id.equals("true") || id.equals("false")) {
            return new BooleanExpression(Boolean.parseBoolean(id));
        }
        var symbolExpression = context.getSymbolExpression(id, context);
        if (symbolExpression != null) {
            return symbolExpression;
        }
        throw new NullPointerException("Symbol " + id + " not found");
    }

    private static boolean isValidVariableName(String name, Context context) {
        return !name.equals("true") && !name.equals("false");
    }
}
