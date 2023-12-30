package de.plixo.galactic.high_level.expressions;

public sealed interface HIRExpression
        permits HIRAssign, HIRBlock, HIRBranch, HIRCallNotation, HIRConstruct, HIRDotNotation,
        HIRFunction, HIRIdentifier, HIRNumber, HIRString, HIRVarDefinition {
}
