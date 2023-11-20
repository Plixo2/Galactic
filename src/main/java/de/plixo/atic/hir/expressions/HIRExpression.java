package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;

public sealed interface HIRExpression
        permits HIRArrayAccessNotation, HIRBinaryExpression, HIRBlock, HIRBranch, HIRCallNotation,
        HIRConstruct, HIRDotNotation, HIRIdentifier, HIRNumber, HIRString, HIRUnary,
        HIRUnaryExpression, HIRVarDefinition {
    JsonElement toJson();
}
