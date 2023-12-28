package de.plixo.galactic.types;

import de.plixo.galactic.exception.FlairCheckException;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.MethodCollection;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.tir.expressions.*;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.plixo.galactic.exception.FlairKind.*;

public abstract class Class extends Type {


    public abstract String name();

    public abstract ClassSource getSource();

    public abstract ObjectPath path();

    @Override
    public String toString() {
        return path().toString();
    }

    public abstract boolean isInterface();

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
        if (possibleField != null) {
            if (!possibleField.isStatic()) {
                throw new FlairCheckException(region, FORMAT,
                        STR."Cannot access a non static field \{possibleField.name()} on class");
            }
            return new StaticFieldExpression(region, this, possibleField);
        }
        var possibleMethods = this.getMethods(id, context);
        possibleMethods = possibleMethods.filter(Method::isStatic);
        if (!possibleMethods.isEmpty()) {
            return new StaticMethodExpression(region, new MethodOwner.ClassOwner(this),
                    possibleMethods);
        }
        throw new FlairCheckException(region, NAME,
                STR."Symbol \{id} not found in class \{this.name()}");
    }

    public @Nullable Expression getDotNotation(Region region, Expression expression, String id,
                                               Context context) {
        var possibleField = this.getField(id, context);
        if (possibleField != null) {
            if (possibleField.isStatic()) {
                throw new FlairCheckException(region, FORMAT,
                        STR."Cannot access a static field \{possibleField.name()} on an object");
            }
            return new FieldExpression(region, expression, this, possibleField);
        }
        var possibleMethods = this.getMethods(id, context);
        possibleMethods = possibleMethods.filter(ref -> !ref.isStatic());
        if (!possibleMethods.isEmpty()) {
            return new GetMethodExpression(region, expression, possibleMethods);
        }
        throw new FlairCheckException(expression.region(), UNEXPECTED_TYPE,
                STR."Symbol \{id} not found on Object \{this.name()}");
    }

    public final String getJVMDestination() {
        return path().asSlashString();
    }
}
