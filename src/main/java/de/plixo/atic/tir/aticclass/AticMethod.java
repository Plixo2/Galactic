package de.plixo.atic.tir.aticclass;

import de.plixo.atic.hir.items.HIRMethod;
import de.plixo.atic.tir.expressions.Expression;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.MethodOwner;
import de.plixo.atic.types.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Represents a method, either of a class or a Unit (represented by the {@link MethodOwner}).
 */
@RequiredArgsConstructor
public class AticMethod {

    @Getter
    private final MethodOwner owner;

    @Getter
    private final int access;

    @Getter
    private final String localName;

    @Getter
    private final List<Parameter> parameters;

    @Getter
    private final Type returnType;

    @Getter
    private final @Nullable HIRMethod hirMethod;

    public @Nullable Expression body = null;


    public Method asAMethod() {
        var types = parameters().stream().map(Parameter::type).toList();
        return new Method(access, localName(), returnType(), types, owner());
    }

    @Override
    public String toString() {
        return "AticMethod{" + "owner=" + owner + ", access=" + access + ", localName='" +
                localName + '\'' + ", parameters=" + parameters.size() + ", returnType=" +
                returnType + '}';
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(access);
    }


}
