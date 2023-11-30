package de.plixo.atic.tir.aticclass;

import de.plixo.atic.hir.items.HIRMethod;
import de.plixo.atic.tir.expressions.Expression;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.MethodOwner;
import de.plixo.atic.types.sub.AMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.List;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

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
    private final AType returnType;

    @Getter
    private final @Nullable HIRMethod hirMethod;

    public @Nullable Expression body = null;


    public AMethod asAMethod() {
        var types = parameters().stream().map(Parameter::type).toList();
        return new AMethod(access, localName(), returnType(), types, owner());
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
