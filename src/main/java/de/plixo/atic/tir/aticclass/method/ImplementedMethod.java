package de.plixo.atic.tir.aticclass.method;

import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.Parameter;
import de.plixo.atic.types.sub.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

@RequiredArgsConstructor
public final class ImplementedMethod implements MethodImplementation {
    @Getter
    private final Method toImplement;
    @Getter
    private final AticMethod implementation;

    @Override
    public Method asMethod() {
        var types = implementation.parameters().stream().map(Parameter::type).toList();
        return new Method(ACC_PUBLIC,implementation.localName(), implementation.returnType(), types,
                implementation.owner());
    }
    @Override
    public AticMethod aticMethod() {
        return implementation;
    }

    @Override
    public String toString() {
        return "ImplementedMethod{" + "toImplement=" + toImplement + ", implementation=" +
                implementation + '}';
    }
}
