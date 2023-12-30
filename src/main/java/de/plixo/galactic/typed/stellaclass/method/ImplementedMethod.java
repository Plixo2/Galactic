package de.plixo.galactic.typed.stellaclass.method;

import de.plixo.galactic.typed.stellaclass.Parameter;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

@Getter
@RequiredArgsConstructor
public final class ImplementedMethod implements MethodImplementation {
    private final Method toImplement;
    private final StellaMethod implementation;

    @Override
    public Method asMethod() {
        var types = implementation.parameters().stream().map(Parameter::type).toList();
        return new Method(ACC_PUBLIC, implementation.localName(), implementation.returnType(),
                types, implementation.owner());
    }

    @Override
    public StellaMethod stellaMethod() {
        return implementation;
    }

    @Override
    public String toString() {
        return "ImplementedMethod{" + "toImplement=" + toImplement + ", implementation=" +
                implementation + '}';
    }
}
