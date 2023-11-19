package de.plixo.atic.types;

import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.types.sub.AField;
import de.plixo.atic.types.sub.AMethod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AClass extends AType implements MethodOwner {

    public abstract ObjectPath path();

    @Override
    public String toString() {
        return "AClass{" + "name='" + path() + "}";
    }


    public abstract boolean isInterface();
    public abstract List<AMethod> getAbstractMethods();
    public abstract List<AMethod> getMethods();

    @Override
    public abstract @Nullable AField getField(String name, Context context);

    @Override
    public abstract MethodCollection getMethods(String name, Context context);

    public abstract @Nullable AClass getSuperClass();


    public abstract List<AClass> getInterfaces();

    @Override
    public char getKind() {
        return 'L';
    }

    @Override
    public String getDescriptor() {
        return getKind() + path().asSlashString() + ";";
    }


}
