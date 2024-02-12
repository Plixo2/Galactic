package de.plixo.galactic.types;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.MethodCollection;
import de.plixo.galactic.typed.expressions.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.plixo.galactic.exception.FlairKind.NAME;


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
            return new StaticMethodExpression(region, possibleMethods);
        }
        throw new FlairCheckException(region, NAME,
                STR."Symbol \{id} not found in class \{this.name()}");
    }

    public @Nullable Expression getDotNotation(Region region, Expression expression, String id,
                                               Context context) {
        var possibleField = this.getField(id, context);
        if (possibleField != null) {
            return new FieldExpression(region, expression, this, possibleField);
        }
        var possibleMethods = this.getMethods(id, context).filter(ref -> !ref.isStatic());
        if (!possibleMethods.isEmpty()) {
            return new GetMethodExpression(region, expression, possibleMethods);
        }
        return null;
    }

    public final String getJVMDestination() {
        return path().asSlashString();
    }

    public Set<Method> implementationLeft(Context context) {
        var set = new HashSet<Method>();
        var superClass = getSuperClass();
        if (superClass != null) {
            set.addAll(superClass.getAbstractMethods());
        }
        for (var anInterface : getInterfaces()) {
            set.addAll(anInterface.getAbstractMethods());
        }
        set.removeIf(ref -> {
            var methodList = getMethods();
            var impls = methodList.stream().filter(me -> !me.isAbstract()).toList();
            for (var impl : impls) {
                if (impl.name().equals(ref.name()) && Method.signatureMatch(ref, impl, context)) {
                    return true;
                }
            }
            return false;
        });
        return set;
    }

    /**
     * Returns the method that is the functional interface method of this class.
     * If the class cannot be used as an interface, null is returned.
     *
     * @return method to implement as a function
     */
    public @Nullable Method functionalInterfaceMethod(Context context) {
        var implementationLeft = this.implementationLeft(context);
        if (implementationLeft.size() != 1) {
            return null;
        }

        return implementationLeft.iterator().next();
    }
}
