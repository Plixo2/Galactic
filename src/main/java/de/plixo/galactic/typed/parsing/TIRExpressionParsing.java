package de.plixo.galactic.typed.parsing;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.exception.FlairKind;
import de.plixo.galactic.high_level.expressions.*;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Converts HIRExpression to a Expression, without any checking
 */
public class TIRExpressionParsing {

    public static Expression parse(HIRExpression expression, Context context) {
        return Objects.requireNonNull(switch (expression) {
            case HIRBlock hirBlock -> parseBlock(hirBlock, context);
            case HIRBranch hirBranch -> parseBranchExpression(hirBranch, context);
            case HIRCallNotation hirCallNotation -> parseCallExpression(hirCallNotation, context);
            case HIRConstruct hirConstruct -> parseConstructExpression(hirConstruct, context);
            case HIRDotNotation hirDotNotation -> parseDotNotation(hirDotNotation, context);
            case HIRIdentifier hirIdentifier -> parseIdentifier(hirIdentifier, context);
            case HIRNumber hirNumber -> parseNumber(hirNumber, context);
            case HIRString hirString -> parseStringExpression(hirString, context);
            case HIRVarDefinition hirVarDefinition -> parseVarDefinition(hirVarDefinition, context);
            case HIRAssign hirAssign -> parseConstructExpression(hirAssign, context);
            case HIRCast hirCast -> parseCast(hirCast, context);
            case HIRCastCheck hirCastCheck -> parseCastCheck(hirCastCheck, context);
            case HIRSuperCall hirSuperCall -> parseSuperCall(hirSuperCall, context);
            case HIRThis hirThis -> parseThis(hirThis, context);
            case HIRWhile hirWhile -> parseWhile(hirWhile, context);
            case HIRBinary hirBinary -> parseBinary(hirBinary, context);
            case HIRUnary hirUnary -> parseUnary(hirUnary, context);
        }, expression.getClass().getName());
    }

    private static Expression parseUnary(HIRUnary hirUnary, Context context) {
        var operator = hirUnary.operator();
        var expression = parse(hirUnary.value(), context);
        var functionName = operator.toFunctionName();
        var function = new DotNotation(hirUnary.region(), expression, functionName);
        var call = new CallNotation(hirUnary.region(), function, new ArrayList<>());
        return call;
    }

    private static Expression parseBinary(HIRBinary hirUnary, Context context) {
        var operator = hirUnary.operator();
        var left = parse(hirUnary.left(), context);
        var right = parse(hirUnary.right(), context);
        var functionName = operator.toFunctionName();
        var function = new DotNotation(hirUnary.region(), left, functionName);
        var call = new CallNotation(hirUnary.region(), function, List.of(right));
        return call;
    }

    private static Expression parseWhile(HIRWhile hirWhile, Context context) {
        var condition = parse(hirWhile.condition(), context);
        var body = parse(hirWhile.body(), context);
        return new WhileExpression(hirWhile.region(), condition, body);
    }

    private static Expression parseThis(HIRThis hirThis, Context context) {
        if (context.thisContext() == null) {
            throw new FlairCheckException(hirThis.region(), FlairKind.UNKNOWN_TYPE,
                    "this is not defined in this context");
        }
        return new ThisExpression(hirThis.region(), context.thisContext());
    }

    private static Expression parseSuperCall(HIRSuperCall superCall, Context context) {
        throw new NullPointerException("SuperCallExpression not implemented");
        //. return new SuperCallExpression(superCall.region(), superCall.superType(), superCall.method(),
        //       superCall.arguments().stream().map(ref -> parse(ref, context)).toList());
    }

    private static CastCheckExpression parseCastCheck(HIRCastCheck cast, Context context) {
        var type = TIRTypeParsing.parse(cast.type(), context);
        var parsed = parse(cast.object(), context);
        return new CastCheckExpression(cast.region(), parsed, type);
    }

    private static CastExpression parseCast(HIRCast cast, Context context) {
        var type = TIRTypeParsing.parse(cast.type(), context);
        var parsed = parse(cast.object(), context);
        return new CastExpression(cast.region(), parsed, type);
    }

    private static AssignExpression parseConstructExpression(HIRAssign hirAssign, Context context) {
        return new AssignExpression(hirAssign.region(), parse(hirAssign.left(), context),
                parse(hirAssign.right(), context));
    }

    private static ConstructExpression parseConstructExpression(HIRConstruct hirConstruct,
                                                                Context context) {
        var type = TIRTypeParsing.parse(hirConstruct.hirType(), context);
        var arguments =
                hirConstruct.parameters().stream().map(ref -> parse(ref.value(), context)).toList();
        return new ConstructExpression(hirConstruct.region(), type, arguments);
    }

    private static StringExpression parseStringExpression(HIRString hirString, Context context) {
        return new StringExpression(hirString.region(), hirString.string());
    }

    private static BranchExpression parseBranchExpression(HIRBranch hirBranch, Context context) {
        var condition = parse(hirBranch.condition(), context);
        var code = parse(hirBranch.body(), context);
        Expression elseExpression = null;
        if (hirBranch.elseBody() != null) {
            elseExpression = parse(hirBranch.elseBody(), context);
        }

        return new BranchExpression(hirBranch.region(), condition, code, elseExpression);
    }

    private static VarDefExpression parseVarDefinition(HIRVarDefinition varDefinition,
                                                       Context context) {
        Type type;
        if (varDefinition.type() != null) {
            type = TIRTypeParsing.parse(varDefinition.type(), context);
        } else {
            type = null;
        }
        var expression = parse(varDefinition.value(), context);
        var name = varDefinition.name();
        return new VarDefExpression(varDefinition.region(), name, type, expression, null);
    }

    private static NumberExpression parseNumber(HIRNumber number, Context context) {
        return new NumberExpression(number.region(), number.value(), number.type());
    }

    private static DotNotation parseDotNotation(HIRDotNotation dotNotation, Context context) {
        var obj = parse(dotNotation.object(), context);
        var id = dotNotation.id();
        return new DotNotation(dotNotation.region(), obj, id);
    }

    private static CallNotation parseCallExpression(HIRCallNotation callNotation, Context context) {
        var obj = parse(callNotation.object(), context);
        var arguments = callNotation.arguments().stream().map(ref -> parse(ref, context)).toList();
        return new CallNotation(callNotation.region(), obj, arguments);
    }

    private static SymbolExpression parseIdentifier(HIRIdentifier identifier, Context context) {
        var id = identifier.id();
        return new SymbolExpression(identifier.region(), id);
    }

    private static BlockExpression parseBlock(HIRBlock expression, Context context) {
        var list = expression.expressions().stream()
                .map(expr -> TIRExpressionParsing.parse(expr, context)).toList();
        return new BlockExpression(expression.region(), list);
    }
}
