package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.types.HIRType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class HIRVarDefinition implements HIRExpression{

    @Getter
    private final String name;
    @Getter
    private final HIRType type;
    @Getter
    private final HIRExpression value;

}
