package de.plixo.atic.hir.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class HIRDotNotation implements HIRExpression{

    @Getter
    private final HIRExpression object;
    @Getter
    private final String id;

}
