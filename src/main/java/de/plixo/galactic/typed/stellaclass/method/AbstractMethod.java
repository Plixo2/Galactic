package de.plixo.galactic.typed.stellaclass.method;

import de.plixo.galactic.typed.stellaclass.Parameter;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
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
        return new Method(ACC_PUBLIC | ACC_ABSTRACT, method.localName(), method.returnType(), types,
                method.owner());
    }

    @Override
    public StellaMethod stellaMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "AbstractMethod{" + "method=" + method + '}';
    }
}
