package de.plixo.atic.hir.expressions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public final class HIRCallNotation implements HIRExpression {

    private final HIRExpression object;
    private final List<HIRExpression> arguments;


}
