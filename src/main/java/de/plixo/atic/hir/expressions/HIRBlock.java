package de.plixo.atic.hir.expressions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRBlock implements HIRExpression {

    @Getter
    private final List<HIRExpression> expressions;

}
