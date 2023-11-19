package de.plixo.atic.tir.expressions;

import de.plixo.atic.types.AType;
import de.plixo.atic.types.sub.AMethod;
import de.plixo.atic.tir.Context;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class MethodInvokeExpression extends Expression {
    @Getter
    private final @Nullable Expression object;
    @Getter
    private final AMethod method;
    @Getter
    private final List<Expression> arguments;

    @Override
    public Expression dotNotation(String id, Context context) {
        return standartDotExpression(method.returnType,id, context);
       // return new ObjectFieldExpression(this,
        //        Objects.requireNonNull(method.returnType.getField(id, context),
        //                "cant find field " + id + " on " + method.returnType));
    }


    @Override
    public AType getType() {
        return method.returnType;
    }
}
