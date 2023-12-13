package de.plixo.atic.hir.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.expressions.HIRExpression;
import de.plixo.atic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class HIRMethod {

    @Getter
    private final String methodName;
    @Getter
    private final List<Parameter> parameters;
    @Getter
    private final HIRType returnType;

    @Getter
    private final HIRExpression expression;


    @RequiredArgsConstructor
    public static class Parameter {
        @Getter
        private final String name;
        @Getter
        private final HIRType type;

    }
}
