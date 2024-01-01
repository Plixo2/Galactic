package de.plixo.galactic.high_level.items;

import de.plixo.galactic.high_level.expressions.HIRExpression;
import de.plixo.galactic.high_level.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class HIRMethod {

    private final String methodName;
    private final List<HIRParameter> HIRParameters;
    private final @Nullable HIRType returnType;
    private final HIRExpression expression;


}
