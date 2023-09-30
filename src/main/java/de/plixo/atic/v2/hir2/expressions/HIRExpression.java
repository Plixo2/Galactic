package de.plixo.atic.v2.hir2.expressions;

import com.google.gson.JsonElement;

public sealed interface HIRExpression
        permits HIRArrayAccessNotation, HIRBinaryExpression, HIRBlock, HIRBranch, HIRCallNotation,
        HIRConstruct, HIRDotNotation, HIRIdentifier, HIRNumber, HIRString, HIRUnaryExpression,
        HIRVarDefinition {
    JsonElement toJson();
}
