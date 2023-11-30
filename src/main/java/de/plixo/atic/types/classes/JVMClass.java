package de.plixo.atic.types.classes;

import de.plixo.atic.tir.ByteCodeMemo;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.Converter;
import de.plixo.atic.types.sub.AField;
import de.plixo.atic.types.sub.AMethod;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JVMClass extends AClass {

    private final ObjectPath name;
    private final ClassNode classNode;

    @SneakyThrows
    public JVMClass(String name) {
        var className = Type.getObjectType(name).getClassName();
        var cn = new ClassNode();
        var cr = new ClassReader(className);
        cr.accept(cn, 0);
        this.name = new ObjectPath(className, ".");
        this.classNode = cn;
    }


    @Override
    public ObjectPath path() {
        return name;
    }

    @SneakyThrows
    @Override
    public boolean isInterface() {
        return Modifier.isInterface(classNode.access);
    }

    @Override
    public List<AMethod> getAbstractMethods() {
        var list = new ArrayList<AMethod>();
        for (var method : classNode.methods) {
            if (Modifier.isAbstract(method.access)) {
                list.add(Converter.getMethod(this, method));
            }
        }
        return list;
    }

    @Override
    public List<AMethod> getMethods() {
        var list = new ArrayList<AMethod>();
        for (var method : classNode.methods) {
            list.add(Converter.getMethod(this, method));
        }
        var superClass = getSuperClass();
        if (superClass != null) {
            list.addAll(superClass.getMethods());
        }
        for (var anInterface : getInterfaces()) {
            list.addAll(anInterface.getMethods());
        }

        return list;
    }


    @Override
    public @Nullable AField getField(String name, Context context) {
        try {
            for (var field : classNode.fields) {
                if (field.name.equals(name)) {
                    return Converter.getField(this, field);
                }
            }
            var superClass = getSuperClass();
            if (superClass != null) {
                return superClass.getField(name, context);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(name, e);
        }
    }

    @Override
    public MethodCollection getMethods(String name, Context context) {
        var list = new ArrayList<AMethod>();
        for (var field : classNode.methods) {
            if (field.name.equals(name)) {
                list.add(Converter.getMethod(this, field));
            }
        }
        var methods = new MethodCollection(name, list);
        var superClass = getSuperClass();
        if (superClass != null) {
            var superClassMethods = superClass.getMethods(name, context);
            methods = methods.join(superClassMethods);
        }
        return methods;
    }

    @Override
    public @Nullable AClass getSuperClass() {
        if (classNode.superName == null) {
            return null;
        }
        return new JVMClass(classNode.superName);
    }

    @SneakyThrows
    @Override
    public List<AClass> getInterfaces() {
        var interfaces = new ArrayList<AClass>();
        for (String anInterface : classNode.interfaces) {
            interfaces.add(new JVMClass(anInterface));
        }
        return interfaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JVMClass jvmClass = (JVMClass) o;
        return Objects.equals(path(), jvmClass.path());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
