package de.plixo.atic.tir.stages;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.Scope;
import de.plixo.atic.tir.expressions.*;

public class Symbols implements Tree {


    @Override
    public Expression defaultBehavior(Expression expression) {
        throw new NullPointerException(
                "Expression of type " + expression.getClass().getSimpleName() +
                        " not implemented for Symbol stage");
    }

    @Override
    public Expression parseConstructExpression(ConstructExpression expression, Context context) {
        var parsed = expression.arguments().stream().map(ref -> parse(ref, context)).toList();
        return new ConstructExpression(expression.constructType(), parsed);
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
    public Expression parseDotNotation(DotNotation expression, Context context) {
        var parsed = parse(expression.object(), context);
        if (parsed instanceof Path path) {
            var newElement = path.path().add(expression.id());
            //TODO check if valid class, otherwise return new path
            return new Path(newElement);
        }
        return new DotNotation(parsed, expression.id());
    }

    @Override
    public Expression parseCallNotation(CallNotation expression, Context context) {
        var parsed = parse(expression.object(), context);
        var arguments = expression.arguments().stream().map(ref -> parse(ref, context)).toList();
        return new CallNotation(parsed, arguments);
    }

    @Override
    public Expression parseBranchExpression(BranchExpression expression, Context context) {
        var parsedCondition = parse(expression.condition(), context);
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
        context.pushScope(new Scope(context.scope()));
        var parsed =
                blockExpression.expressions().stream().map(ref -> parse(ref, context)).toList();
        context.popScope();
        return new BlockExpression(parsed);
    }

    @Override
    public Expression parseVarDefExpression(VarDefExpression varDefExpression, Context context) {
        var scope = context.scope();
        scope.addVariable(varDefExpression.name(), 0);
        var parsed = parse(varDefExpression.expression(), context);
        return new VarDefExpression(varDefExpression.name(), varDefExpression.hint(), parsed);
    }

    @Override
    public Expression parseSymbolExpression(SymbolExpression expression, Context context) {
        var id = expression.id();
        if (id.equals("true") || id.equals("false")) {
            return new BooleanExpression(Boolean.parseBoolean(id));
        }
        var variable = context.scope().getVariable(id);
        if (variable != null) {
            return new VarExpression(variable);
        } else {
            return new Path(new ObjectPath(id));
        }
    }
}
