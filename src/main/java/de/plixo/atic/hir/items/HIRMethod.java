package de.plixo.atic.hir.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.hir.expressions.HIRExpression;
import de.plixo.atic.hir.types.HIRType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public final class HIRMethod {

    private final String methodName;
    private final List<Parameter> parameters;
    private final HIRType returnType;
    private final HIRExpression expression;


    @Getter
    @RequiredArgsConstructor
    public static class Parameter {
        private final String name;
        private final HIRType type;

    }
}
