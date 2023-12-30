package de.plixo.galactic.typed.stellaclass.method;

import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Method;

/**
 * Represents the type of method implementation.
 * A method implementation is either a {@link AbstractMethod}, {@link ImplementedMethod} or {@link NewMethod}.
 */
public sealed interface MethodImplementation permits AbstractMethod, ImplementedMethod, NewMethod {
    Method asMethod();

    StellaMethod stellaMethod();
}
