package de.plixo.galactic.hir.expressions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public final class HIRCallNotation implements HIRExpression {

    private final HIRExpression object;
    private final List<HIRExpression> arguments;


}
