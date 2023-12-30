package de.plixo.galactic.boundary;

import de.plixo.galactic.common.ObjectPath;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.ClassSource;
import de.plixo.galactic.types.Field;
import de.plixo.galactic.types.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a class loaded into the JVM
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = false)
@Setter
public class JVMLoadedClass extends Class {

    @Getter
    private final ClassNode node;
    @Getter
    private final byte[] data;
    private final ObjectPath path;
    private final String name;
    private int access = 0;
    private @Nullable Class superClass = null;
    private List<Class> interfaces = new ArrayList<>();
    private List<Method> methods = new ArrayList<>();
    private List<Field> fields = new ArrayList<>();


    @Override
    public String name() {
        return name;
    }

    @Override
    public ClassSource getSource() {
        return new ClassSource.JVMSource(node);
    }

    @Override
    public ObjectPath path() {
        return path;
    }

    @Override
    public boolean isInterface() {
        return Modifier.isInterface(access);
    }

    @Override
    public List<Method> getAbstractMethods() {
        return methods.stream().filter(Method::isAbstract).toList();
    }

    @Override
    public List<Method> getMethods() {
        var list = new ArrayList<>(methods);
        if (superClass != null) {
            list.addAll(superClass.getMethods());
        }
        for (var anInterface : interfaces) {
            list.addAll(anInterface.getMethods());
        }
        //TODO check collisions in check stage
        return list;
    }

    @Override
    public List<Field> getFields() {
        var list = new ArrayList<>(fields);
        if (superClass != null) {
            list.addAll(superClass.getFields());
        }
        //unnecessary, interfaces cant have fields, in theories for now at least
        for (var anInterface : interfaces) {
            list.addAll(anInterface.getFields());
        }
        //TODO check collisions in check stage
        return list;
    }


    @Override
    public @Nullable Class getSuperClass() {
        return superClass;
    }

    @Override
    public List<Class> getInterfaces() {
        return interfaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JVMLoadedClass commonClass = (JVMLoadedClass) o;
        return Objects.equals(path(), commonClass.path());
    }

    @Override
    public int hashCode() {
        return Objects.hash(path());
    }

}
