package de.plixo.atic.tir.parsing;

import de.plixo.atic.hir.expressions.*;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.expressions.*;

import java.util.List;
import java.util.Objects;

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
        }, expression.getClass().getName());
    }


    private static Expression parseConstructExpression(HIRConstruct hirConstruct, Context context) {
        var type = TIRTypeParsing.parse(hirConstruct.hirType(), context);
        var arguments =
                hirConstruct.parameters().stream().map(ref -> parse(ref.value(), context)).toList();
        return new ConstructExpression(type, arguments);
    }

    private static Expression parseStringExpression(HIRString hirString, Context context) {
        return new StringExpression(hirString.string());
    }

    private static Expression parseBranchExpression(HIRBranch hirBranch, Context context) {
        var condition = parse(hirBranch.condition(), context);
        var code = parse(hirBranch.body(), context);
        Expression elseExpression = null;
        if (hirBranch.elseBody() != null) {
            elseExpression = parse(hirBranch.elseBody(), context);
        }

        return new BranchExpression(condition, code, elseExpression);
    }

    private static Expression parseVarDefinition(HIRVarDefinition varDefinition, Context context) {
        var type = TIRTypeParsing.parse(varDefinition.type(), context);
        var expression = parse(varDefinition.value(), context);
        var name = varDefinition.name();
        return new VarDefExpression(name, type, expression);
    }

    private static Expression parseNumber(HIRNumber number, Context context) {
        return new NumberExpression(number.number());
    }

    private static Expression parseDotNotation(HIRDotNotation dotNotation, Context context) {
        var obj = parse(dotNotation.object(), context);
        var id = dotNotation.id();
        return new DotNotation(obj, id);
    }

    private static Expression parseCallExpression(HIRCallNotation callNotation, Context context) {
        var obj = parse(callNotation.object(), context);
        var arguments = callNotation.arguments().stream().map(ref -> parse(ref, context)).toList();
        return new CallNotation(obj, arguments);
    }

    private static Expression parseIdentifier(HIRIdentifier identifier, Context context) {
        var id = identifier.id();
        return new SymbolExpression(id);
    }

    private static Expression parseBlock(HIRBlock expression, Context context) {
        var list = expression.expressions().stream()
                .map(expr -> TIRExpressionParsing.parse(expr, context)).toList();
        return new BlockExpression(list);
    }
}
