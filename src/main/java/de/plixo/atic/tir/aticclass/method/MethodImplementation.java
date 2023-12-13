package de.plixo.atic.tir.aticclass.method;

import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.types.Method;

/**
 * Represents the type of method implementation.
 * A method implementation is either a {@link AbstractMethod}, {@link ImplementedMethod} or {@link NewMethod}.
 */
public sealed interface MethodImplementation permits AbstractMethod, ImplementedMethod, NewMethod {
    Method asMethod();
    AticMethod aticMethod();
}
