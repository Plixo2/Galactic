package de.plixo.atic.types;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.tir.ObjectPath;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Class extends Type {


    public abstract String name();
    public abstract ClassSource getSource();
    public abstract ObjectPath path();

    @Override
    public String toString() {
        return "AClass{" + "name='" + path() + "}";
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


}
