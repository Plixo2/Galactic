package de.plixo.atic.tir.aticclass.method;

import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.Parameter;
import de.plixo.atic.types.sub.AMethod;
import lombok.RequiredArgsConstructor;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

@RequiredArgsConstructor
public final class AbstractMethod implements MethodImplementation {
    private final AticMethod method;

    @Override
    public AMethod asMethod() {
        var types = method.parameters().stream().map(Parameter::type).toList();
        return new AMethod(ACC_PUBLIC | ACC_ABSTRACT, method.localName(), method.returnType(),
                types, method.owner());
    }

    @Override
    public AticMethod aticMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "AbstractMethod{" + "method=" + method + '}';
    }
}
