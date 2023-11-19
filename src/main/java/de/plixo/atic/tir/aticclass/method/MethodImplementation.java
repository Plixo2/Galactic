package de.plixo.atic.tir.aticclass.method;

import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.types.sub.AMethod;

public sealed interface MethodImplementation permits AbstractMethod, ImplementedMethod, NewMethod {
    AMethod asMethod();
    AticMethod aticMethod();
}
