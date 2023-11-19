package de.plixo.atic.tir.aticclass.method;

import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.types.sub.AMethod;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class NewMethod implements MethodImplementation {

    private final AticMethod method;
    @Override
    public AMethod asMethod() {
        return method.asAMethod();
    }

    @Override
    public AticMethod aticMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "NewMethod{" + "method=" + method + '}';
    }
}
