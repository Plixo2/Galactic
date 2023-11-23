package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;

public sealed interface HIRExpression
        permits HIRBlock, HIRBranch, HIRCallNotation,
        HIRConstruct, HIRDotNotation, HIRIdentifier, HIRNumber, HIRString, HIRVarDefinition {
    JsonElement toJson();
}
