package de.plixo.galactic.tir.parsing;

import de.plixo.galactic.hir.expressions.*;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.expressions.*;
import de.plixo.galactic.types.Type;

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
        }, expression.getClass().getName());
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
