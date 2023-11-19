package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;
import de.plixo.atic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class HIRCast implements HIRExpression {
    private final HIRExpression object;
    private final HIRType type;

    @Override
    public JsonElement toJson() {
        return null;
    }
}
