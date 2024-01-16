package de.plixo.galactic.typed.stellaclass;

import de.plixo.galactic.high_level.expressions.HIRExpression;
import de.plixo.galactic.lexer.Region;
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
@Getter
public class StellaMethod {
    private final int access;
    private final String localName;
    @Setter
    private List<Parameter> parameters;
    private final Type returnType;
    private final HIRExpression hirExpression;
    private final MethodOwner owner;
    @Setter
    private @Nullable Expression body = null;
    @Setter
    private @Nullable Type thisContext;

    @Getter
    private final @Nullable Type extension;

    @Getter
    private final Region region;

    public StellaMethod(int access, String localName, List<Parameter> parameters, Type returnType,
                        HIRExpression hirExpression, MethodOwner owner, @Nullable Type extension,
                        Region region) {
        this.access = access;
        this.localName = localName;
        this.parameters = parameters;
        this.returnType = returnType;
        this.hirExpression = hirExpression;
        this.owner = owner;
        this.extension = extension;
        this.region = region;
    }

    public Method asMethod() {
        var types = parameters().stream().map(Parameter::type).toList();
        return new Method(access, localName(), returnType(), types, owner);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(access);
    }

    public boolean isStatic() {
        return Modifier.isStatic(access);
    }

    public boolean isConstructor() {
        return localName.equals("<init>");
    }
}
