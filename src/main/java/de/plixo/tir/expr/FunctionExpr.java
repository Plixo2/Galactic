package de.plixo.tir.expr;

import de.plixo.tir.scoping.Scope;
import de.plixo.typesys.types.FunctionType;
import de.plixo.typesys.types.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class FunctionExpr implements Expr {

    @Getter
    private final Scope scope;

    @Getter
    private final Type returnType;

    @Getter
    private final List<Variable> arguments;

    @Getter
    @Setter
    @Accessors(fluent = false)
    private @Nullable Expr body = null;

    public FunctionExpr(Scope scope, Type returnType, List<Variable> arguments) {
        this.scope = scope;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    @Override
    public Type getType() {
        return new FunctionType(returnType, arguments.stream().map(ref -> ref.type).toList());
    }

    @Override
    public void fillType() {

    }

    @AllArgsConstructor
    public static class Variable {
        @Getter
        private final String name;
        @Getter
        private final Type type;
    }
}
