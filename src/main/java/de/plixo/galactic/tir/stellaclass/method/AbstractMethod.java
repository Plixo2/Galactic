package de.plixo.galactic.tir.stellaclass.method;

import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.tir.stellaclass.Parameter;
import de.plixo.galactic.types.Method;
import lombok.RequiredArgsConstructor;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

@RequiredArgsConstructor
public final class AbstractMethod implements MethodImplementation {
    private final StellaMethod method;

    @Override
    public Method asMethod() {
        var types = method.parameters().stream().map(Parameter::type).toList();
        return new Method(ACC_PUBLIC | ACC_ABSTRACT, method.localName(), method.returnType(),
                types, method.owner());
    }

    @Override
    public StellaMethod aticMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "AbstractMethod{" + "method=" + method + '}';
    }
}
