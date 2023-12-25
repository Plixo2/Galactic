package de.plixo.galactic.types;

import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.MethodCollection;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.tir.expressions.*;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    @Override
    public abstract @Nullable Field getField(String name, Context context);

    @Override
    public abstract MethodCollection getMethods(String name, Context context);

    public abstract @Nullable Class getSuperClass();


    public abstract List<Class> getInterfaces();

    @Override
    public char getKind() {
        return 'L';
    }

    @Override
    public String getDescriptor() {
        return getKind() + path().asSlashString() + ";";
    }

    public @Nullable Expression getStaticDotNotation(Region region, String id, Context context) {
        var possibleField = this.getField(id, context);
        if (possibleField != null) {
            return new StaticFieldExpression(region, this, possibleField);
        }
        var possibleMethods = this.getMethods(id, context);
        if (!possibleMethods.isEmpty()) {
            return new StaticMethodExpression(region, new MethodOwner.ClassOwner(this),
                    possibleMethods);
        }
        return null;
    }

    public @Nullable Expression getDotNotation(Region region, Expression expression, String id,
                                               Context context) {
        var possibleField = this.getField(id, context);
        if (possibleField != null) {
            return new FieldExpression(region, expression, this, possibleField);
        }
        var possibleMethods = this.getMethods(id, context);
        if (!possibleMethods.isEmpty()) {
            return new GetMethodExpression(region, expression, possibleMethods);
        }
        return null;
    }

    public String getJVMDestination() {
        return path().asSlashString();
    }
}
