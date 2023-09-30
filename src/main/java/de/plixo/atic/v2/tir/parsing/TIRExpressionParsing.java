package de.plixo.atic.v2.tir.parsing;

import de.plixo.atic.v2.hir2.expressions.*;
import de.plixo.atic.v2.tir.Context;
import de.plixo.atic.v2.tir.expressions.*;
import de.plixo.atic.v2.tir.type.JVMClassType;
import de.plixo.atic.v2.tir.type.Primitive;
import de.plixo.atic.v2.tir.type.Typing;

import java.util.List;
import java.util.Objects;

public class TIRExpressionParsing {

    public static Expression parse(HIRExpression expression, Context context) {

        return Objects.requireNonNull(switch (expression) {
            case HIRArrayAccessNotation hirArrayAccessNotation -> null;
            case HIRBinaryExpression hirBinaryExpression -> null;
            case HIRBlock hirBlock -> parseBlock(hirBlock, context);
            case HIRCallNotation hirCallNotation -> parseCallNotation(hirCallNotation, context);
            case HIRDotNotation hirDotNotation -> parseDotNotation(hirDotNotation, context);
            case HIRIdentifier hirIdentifier -> parseIdentifier(hirIdentifier, context);
            case HIRNumber hirNumber -> parseNumber(hirNumber, context);
            case HIRString hirString -> parseString(hirString, context);
            case HIRUnaryExpression hirUnaryExpression -> null;
            case HIRVarDefinition hirVarDefinition -> parseVarDefinition(hirVarDefinition, context);
            case HIRBranch hirBranch -> parseBranch(hirBranch, context);
            case HIRConstruct hirConstruct -> parseConstruct(hirConstruct,context);
        }, expression.getClass().getName());
    }

    private static Expression parseConstruct(HIRConstruct construct, Context context) {
        var hirType = TIRTypeParsing.parse(construct.hirType(), context);
        if (hirType instanceof JVMClassType jvmClassType) {
            return jvmClassType.construct(construct, context);
        } else {
            throw new NullPointerException("not supported");
        }
    }

    private static BranchExpression parseBranch(HIRBranch branch, Context context) {
        var condition = TIRExpressionParsing.parse(branch.condition(), context);
        var conditionType = condition.asAticType();
        assert Typing.isAssignableFrom(Primitive.BOOLEAN, conditionType) :
                "cant assign " + conditionType + " to type " + Primitive.BOOLEAN;
        var childContext = context.childContext();
        var body = TIRExpressionParsing.parse(branch.body(), childContext);
        return new BranchExpression(condition, body);
    }

    private static Expression parseBlock(HIRBlock expression, Context context) {
        var childContext = context.childContext();
        var list = expression.expressions().stream()
                .map(expr -> TIRExpressionParsing.parse(expr, childContext)).toList();
        return new ExpressionBlock(list);
    }

    private static Expression parseNumber(HIRNumber number, Context context) {
        return new NumberExpression(number.number());
    }

    private static Expression parseVarDefinition(HIRVarDefinition varDefinition, Context context) {
        var parse = TIRExpressionParsing.parse(varDefinition.value(), context);
        var type = TIRTypeParsing.parse(varDefinition.type(), context);
        System.out.println("Defined " + varDefinition.name());
        System.out.println("from type " + type);
        var valueType = parse.asAticType();
        assert Typing.isAssignableFrom(type, valueType) :
                "cant assign " + valueType + " to type " + type;
        var variable = context.addVariable(varDefinition.name(), type, Context.VariableType.LOCAL);
        return new VariableDefinition(variable, parse);
    }

    private static Expression parseIdentifier(HIRIdentifier identifier, Context context) {
        var id = identifier.id();
        if (id.equals("true") || id.equals("false")) {
            return new BoolExpression(Boolean.parseBoolean(id));
        }
        var variable = context.getVariable(id);
        if (variable != null) {
            return new VariableExpression(variable);
        }
        return new Path(List.of(id));
    }

    public static Expression parseCallNotation(HIRCallNotation callNotation, Context context) {
        var object = TIRExpressionParsing.parse(callNotation.object(), context);
        return object.callNotation(callNotation.arguments(), context);
    }

    public static Expression parseDotNotation(HIRDotNotation dotNotation, Context context) {
        var object = TIRExpressionParsing.parse(dotNotation.object(), context);
        return object.dotNotation(dotNotation.id(), context);
    }

    public static Expression parseString(HIRString string, Context context) {
        return new JVMString(string.string());
    }
}
