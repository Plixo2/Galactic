package de.plixo.galactic.typed.expressions;

import de.plixo.galactic.high_level.expressions.HIRFunction;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.plixo.galactic.typed.Scope.INPUT;


public record FunctionExpression(Region region, List<FunctionalParameter> inputVariable,
                                 @Nullable Type interfaceTypeHint, @Nullable Type returnTypeHint,
                                 Expression expression, List<Scope.ClosureVariable> usedVariables) implements Expression {
    @Override
    public Type getType(Context context) {
        return null;
    }

    @Getter
    public static class FunctionalParameter {
        private final String name;
        private final @Nullable Type type;
        private final Scope.Variable variable;

        public FunctionalParameter(String name, @Nullable Type type) {
            this.name = name;
            this.type = type;
            this.variable = new Scope.Variable(name, INPUT, type);
        }
    }
}
