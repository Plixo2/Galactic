package de.plixo.atic.v2.tir;

import de.plixo.atic.v2.hir2.items.HIRFunction;
import de.plixo.atic.v2.tir.expressions.Expression;
import de.plixo.atic.v2.tir.type.Primitive;
import de.plixo.atic.v2.tir.type.Type;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Method {

    private final String localName;
    private final Unit unit;

    @Getter
    private final HIRFunction hir;
    @Getter
    private List<Parameter> parameters = new ArrayList<>();
    private Type returnType = Primitive.VOID;

    private @Nullable Expression expression = null;


    public String name() {
        return unit.name() + "." + localName;
    }

    public Parameter addParameter(String name, Type type) {
        var parameter = new Parameter(name, type);
        parameters.add(parameter);
        return parameter;
    }

    public void setReturnType(Type type) {
        this.returnType = type;
    }

    public void setExpression(@NotNull Expression expression) {
        this.expression = expression;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Parameter {
        @Getter
        private final String name;
        @Getter
        private final Type type;


        public Context.Variable addTo(Context context) {
            return context.addVariable(name, type, Context.VariableType.INPUT);
        }
    }
}
