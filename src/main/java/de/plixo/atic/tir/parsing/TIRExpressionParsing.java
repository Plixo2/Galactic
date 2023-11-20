package de.plixo.atic.tir.parsing;

import de.plixo.atic.hir.expressions.*;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.expressions.*;
import de.plixo.atic.types.AArray;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.APrimitive;
import de.plixo.atic.types.AType;

import java.util.Objects;

public class TIRExpressionParsing {

    public static Expression parse(HIRExpression expression, Context context) {
        return Objects.requireNonNull(switch (expression) {
            case HIRUnary hirUnary -> parseUnaryExpression(hirUnary, context);
            case HIRArrayAccessNotation hirArrayAccessNotation -> null;
            case HIRBinaryExpression hirBinaryExpression -> null;
            case HIRBlock hirBlock -> parseBlock(hirBlock, context);
            case HIRBranch hirBranch -> parseBranchExpression(hirBranch, context);
            case HIRCallNotation hirCallNotation -> parseCallExpression(hirCallNotation, context);
            case HIRConstruct hirConstruct -> parseConstructExpression(hirConstruct, context);
            case HIRDotNotation hirDotNotation -> parseDotNotation(hirDotNotation, context);
            case HIRIdentifier hirIdentifier -> parseIdentifier(hirIdentifier, context);
            case HIRNumber hirNumber -> parseNumber(hirNumber, context);
            case HIRString hirString -> parseStringExpression(hirString, context);
            case HIRUnaryExpression hirUnaryExpression -> null;
            case HIRVarDefinition hirVarDefinition -> parseVarDefinition(hirVarDefinition, context);
        }, expression.getClass().getName());
    }

    private static Expression parseUnaryExpression(HIRUnary unary, Context context) {
        var object = parse(unary.object(), context);
        var expression = new UnaryExpression(object, unary.function());
        expression.validate();
        return expression;
    }

    private static Expression parseConstructExpression(HIRConstruct hirConstruct, Context context) {
        var type = TIRTypeParsing.parse(hirConstruct.hirType(), context);
        if (type instanceof AClass aClass) {
            var methods = aClass.getMethods("<init>", context);
            var expressions =
                    hirConstruct.parameters().stream().map(ref -> parse(ref.value(), context))
                            .toList();
            var typeList = expressions.stream().map(Expression::getType).toList();
            var init = methods.find(typeList, context);
            if (init == null) {
                throw new NullPointerException("cant find constructor " + typeList);
            }
            return new ConstructExpression(aClass, init, expressions);
        } else if (type instanceof AArray array) {
            var expressions =
                    hirConstruct.parameters().stream().map(ref -> parse(ref.value(), context))
                            .toList();
            var elementType = array.elementType;
            for (var expression : expressions) {
                var foundType = expression.getType();
                if (!AType.isAssignableFrom(elementType, foundType, context)) {
                    throw new NullPointerException(
                            "cant assign " + foundType + " to " + elementType);
                }
            }
            return new ArrayConstructExpression(elementType, expressions);
        } else {
            throw new NullPointerException("cant construct type " + type);
        }
    }

    private static Expression parseStringExpression(HIRString hirString, Context context) {
        return new StringExpression(hirString.string());
    }

    private static Expression parseBranchExpression(HIRBranch hirBranch, Context context) {
        var condition =
                TIRTypeConversion.convert(parse(hirBranch.condition(), context), APrimitive.BOOLEAN,
                        context);
        if (condition == null ||
                !AType.isAssignableFrom(APrimitive.BOOLEAN, condition.getType(), context)) {
            throw new NullPointerException("not a boolean expression");
        }
        var code = parse(hirBranch.body(), context.childContext());
        var elsePart = hirBranch.elseBody();
        Expression elseExpression = null;
        if (elsePart != null) {
            elseExpression = parse(elsePart, context);
        }

        return new BranchExpression(condition, code, elseExpression);
    }

    private static Expression parseVarDefinition(HIRVarDefinition varDefinition, Context context) {
        var type = TIRTypeParsing.parse(varDefinition.type(), context);
        var variable = context.addVariable(varDefinition.name(), type, Context.VariableType.LOCAL);
        var expression = parse(varDefinition.value(), context);
        var expressionType = expression.getType();
        var assignable = AType.isAssignableFrom(type, expressionType, context);
        if (!assignable) {
            throw new NullPointerException("no matching types " + type + " & " + expressionType);
        }
        return new VariableDefinitionExpression(variable, type, expression);
    }

    private static Expression parseNumber(HIRNumber number, Context context) {
        return new NumberExpression(number.number());
    }

    private static Expression parseDotNotation(HIRDotNotation dotNotation, Context context) {
        var obj = parse(dotNotation.object(), context);
        return obj.dotNotation(dotNotation.id(), context);
    }

    private static Expression parseCallExpression(HIRCallNotation callNotation, Context context) {
        var obj = parse(callNotation.object(), context);
        return obj.callExpression(callNotation.arguments(), context);
    }

    private static Expression parseIdentifier(HIRIdentifier identifier, Context context) {
        var id = identifier.id();
        if (id.equals("true") || id.equals("false")) {
            return new BooleanExpression(Boolean.parseBoolean(id));
        }
        var variable = context.getVariable(id);
        if (variable != null) {
            return new VariableExpression(variable);
        }
        return new Path(new ObjectPath(id));
    }

    private static Expression parseBlock(HIRBlock expression, Context context) {
        var childContext = context.childContext();
        var list = expression.expressions().stream()
                .map(expr -> TIRExpressionParsing.parse(expr, childContext)).toList();
        return new BlockExpression(list);
    }
//    }

//    private static Expression parseConstruct(HIRConstruct construct, Context context) {
//        var hirType = TIRTypeParsing.parse(construct.hirType(), context);
//        if (hirType instanceof JVMClassType jvmClassType) {
//            return jvmClassType.construct(construct, context);
//        } else {
//            throw new NullPointerException("not supported");
//        }
//    }
//
//    private static BranchExpression parseBranch(HIRBranch branch, Context context) {
//        var condition = TIRExpressionParsing.parse(branch.condition(), context);
//        var conditionType = condition.asAticType();
//        assert Typing.isAssignableFrom(Primitive.BOOLEAN, conditionType) :
//                "cant assign " + conditionType + " to type " + Primitive.BOOLEAN;
//        var childContext = context.childContext();
//        var body = TIRExpressionParsing.parse(branch.body(), childContext);
//        return new BranchExpression(condition, body);
//    }
//
//    private static Expression parseBlock(HIRBlock expression, Context context) {
//        var childContext = context.childContext();
//        var list = expression.expressions().stream()
//                .map(expr -> TIRExpressionParsing.parse(expr, childContext)).toList();
//        return new ExpressionBlock(list);
//    }
//
//    private static Expression parseNumber(HIRNumber number, Context context) {
//        return new NumberExpression(number.number());
//    }
//
//    private static Expression parseVarDefinition(HIRVarDefinition varDefinition, Context context) {
//        var parse = TIRExpressionParsing.parse(varDefinition.value(), context);
//        var type = TIRTypeParsing.parse(varDefinition.type(), context);
//        System.out.println("Defined " + varDefinition.name());
//        System.out.println("from type " + type);
//        var valueType = parse.asAticType();
//        assert Typing.isAssignableFrom(type, valueType) :
//                "cant assign " + valueType + " to type " + type;
//        var variable = context.addVariable(varDefinition.name(), type, Context.VariableType.LOCAL);
//        return new VariableDefinition(variable, parse);
//    }
//
//    private static Expression parseIdentifier(HIRIdentifier identifier, Context context) {
//        var id = identifier.id();
//        if (id.equals("true") || id.equals("false")) {
//            return new BoolExpression(Boolean.parseBoolean(id));
//        }
//        var variable = context.getVariable(id);
//        if (variable != null) {
//            return new VariableExpression(variable);
//        }
//        return new Path(List.of(id));
//    }
//
//    public static Expression parseCallNotation(HIRCallNotation callNotation, Context context) {
//        var object = TIRExpressionParsing.parse(callNotation.object(), context);
//        return object.callNotation(callNotation.arguments(), context);
//    }
//
//    public static Expression parseDotNotation(HIRDotNotation dotNotation, Context context) {
//        var object = TIRExpressionParsing.parse(dotNotation.object(), context);
//        return object.dotNotation(dotNotation.id(), context);
//    }
//
//    public static Expression parseString(HIRString string, Context context) {
//        return new JVMString(string.string());
//    }
}
