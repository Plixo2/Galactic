package de.plixo.galactic.high_level.expressions;

public sealed interface HIRExpression
        permits HIRAssign, HIRBlock, HIRBranch, HIRCallNotation, HIRCast, HIRCastCheck,
        HIRConstruct, HIRDotNotation, HIRIdentifier, HIRNumber, HIRString,
        HIRVarDefinition {
}
