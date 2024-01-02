package de.plixo.galactic.high_level.items;

import de.plixo.galactic.high_level.expressions.HIRExpression;
import de.plixo.galactic.high_level.types.HIRType;
import de.plixo.galactic.lexer.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class HIRMethod {


    private final Region region;
    private final String methodName;
    private final List<HIRParameter> hirParameters;
    private final HIRType returnType;
    private final HIRExpression expression;



}
