package de.plixo.galactic.tir.stellaclass.method;

import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.types.Method;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class NewMethod implements MethodImplementation {

    private final StellaMethod method;
    @Override
    public Method asMethod() {
        return method.asMethod();
    }

    @Override
    public StellaMethod aticMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "NewMethod{" + "method=" + method + '}';
    }
}
