package de.plixo.galactic.typed.stellaclass;

import de.plixo.galactic.high_level.items.HIRMethod;
import de.plixo.galactic.typed.Scope;
import de.plixo.galactic.typed.expressions.Expression;
import de.plixo.galactic.types.Method;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Represents a method, either of a class or a Unit.
 */
@RequiredArgsConstructor
@Getter
public class StellaMethod {
    private final int access;
    private final String localName;
    private final List<Parameter> parameters;
    private final Type returnType;
    private final @Nullable HIRMethod hirMethod;
    private final MethodOwner owner;
    public @Nullable Expression body = null;
    @Setter
    private @Nullable Scope.Variable thisVariable = null;


    public Method asMethod() {
        var types = parameters().stream().map(Parameter::type).toList();
        return new Method(access, localName(), returnType(), types, owner);
    }

    @Override
    public String toString() {
        return STR."Method{access=\{access}, localName='\{localName}\{'\''}, parameters=\{
                parameters}, returnType=\{returnType}, hirMethod=\{hirMethod}, body=\{body}\{'}'}";
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(access);
    }

    public boolean isStatic() {
        return Modifier.isStatic(access);
    }
}
