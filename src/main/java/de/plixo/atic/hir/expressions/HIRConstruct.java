package de.plixo.atic.hir.expressions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRConstruct implements HIRExpression {

    @Getter
    private final HIRType hirType;
    @Getter
    private final List<ConstructParam> parameters;



    @RequiredArgsConstructor
    public static class ConstructParam {
        @Getter
        private final String name;
        @Getter
        private final HIRExpression value;

    }
}
