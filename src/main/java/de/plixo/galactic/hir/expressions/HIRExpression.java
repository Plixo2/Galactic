package de.plixo.galactic.hir.expressions;

public sealed interface HIRExpression
        permits HIRAssign, HIRBlock, HIRBranch, HIRCallNotation, HIRConstruct, HIRDotNotation,
        HIRIdentifier, HIRNumber, HIRString, HIRVarDefinition {
}
