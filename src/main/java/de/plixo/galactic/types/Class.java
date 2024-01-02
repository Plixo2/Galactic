package de.plixo.galactic.types;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.MethodCollection;
import de.plixo.galactic.common.ObjectPath;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.List;

import static de.plixo.galactic.exception.FlairKind.*;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * Base Class type
 */
public abstract class Class extends Type {


    public abstract String name();

    public abstract ClassSource getSource();

    public abstract ObjectPath path();

    @Override
    public String toString() {
        return path().toString();
    }

    public abstract int modifiers();
    public boolean isInterface() {
        return Modifier.isInterface(modifiers());
    }
    public boolean isPublic() {
        return Modifier.isPublic(modifiers());
    }
    public boolean isFinal() {
        return Modifier.isFinal(modifiers());
    }

    public abstract List<Method> getAbstractMethods();

    public abstract List<Method> getMethods();

    public abstract List<Field> getFields();


    public abstract @Nullable Class getSuperClass();

    public abstract List<Class> getInterfaces();

    @Override
    public char getJVMKind() {
        return 'L';
    }

    @Override
    public String getDescriptor() {
        return STR."\{getJVMKind()}\{path().asSlashString()};";
    }

    public @Nullable Field getField(String name, Context context) {
        for (var field : getFields()) {
            if (field.name().equals(name)) {
                return field;
            }
        }
        var superClass = getSuperClass();
        if (superClass != null) {
            return superClass.getField(name, context);
        }
        return null;
    }

    public MethodCollection getMethods(String name, Context context) {
        var aMethods = getMethods().stream().filter(ref -> ref.name().equals(name)).toList();
        var methods = new MethodCollection(name, aMethods);
        var superClass = getSuperClass();
        if (superClass != null) {
            methods = methods.join(superClass.getMethods(name, context));
        }
        return methods;
    }


    public Expression getStaticDotNotation(Region region, String id, Context context) {
        var possibleField = this.getField(id, context);
        if (possibleField != null && possibleField.isStatic()) {
            return new StaticFieldExpression(region, this, possibleField);
        }
        var possibleMethods = this.getMethods(id, context).filter(Method::isStatic);
        if (!possibleMethods.isEmpty()) {
            return new StaticMethodExpression(region, new MethodOwner.ClassOwner(this),
                    possibleMethods);
        }
        throw new FlairCheckException(region, NAME,
                STR."Symbol \{id} not found in class \{this.name()}");
    }

    public Expression getDotNotation(Region region, Expression expression, String id,
                                               Context context) {
        var possibleField = this.getField(id, context);
        if (possibleField != null) {
            return new FieldExpression(region, expression, this, possibleField);
        }
        var possibleMethods = this.getMethods(id, context).filter(ref -> !ref.isStatic());
        if (!possibleMethods.isEmpty()) {
            return new GetMethodExpression(region, expression, possibleMethods);
        }
        throw new FlairCheckException(expression.region(), UNEXPECTED_TYPE,
                STR."Symbol \{id} not found on Object from class \{this.name()}");
    }

    public final String getJVMDestination() {
        return path().asSlashString();
    }
}
