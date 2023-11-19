package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class BranchExpression extends Expression {
    @Getter
    private final Expression condition;
    @Getter
    private final Expression body;
    @Getter
    private final @Nullable Expression elseExpression;


    @Override
    public AType getType() {
        return new AVoid();
    }
}
