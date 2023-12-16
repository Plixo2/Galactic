package de.plixo.atic.boundary;

import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.types.Class;
import de.plixo.atic.types.*;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Helper class to load classes from the JVM, uses {@link LoadedBytecode} for mapping classes to
 * paths
 */
public class JVMLoader {
    public static @Nullable JVMLoadedClass asJVMClass(ObjectPath path, LoadedBytecode bytecode) {
        var loadedClass = bytecode.getClass(path);
        if (loadedClass != null) {
            return loadedClass;
        }
        var stream = getBytecode(path);
        if (stream != null) {
            return generate(path, stream, bytecode);
        }
        return null;
    }

    private static JVMLoadedClass generate(ObjectPath path, InputStream stream,
                                        LoadedBytecode bytecode) {
        var loadedClass = bytecode.getClass(path);
        if (loadedClass != null) {
            return loadedClass;
        }
        var classNode = new ClassNode();
        ClassReader classReader;
        try {
            classReader = new ClassReader(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        classReader.accept(classNode, 0);

        var jvmLoadedClass =
                new JVMLoadedClass(new ClassSource.JVMSource(classNode), path, classNode.name);
        bytecode.putClass(path, jvmLoadedClass);
        generate(jvmLoadedClass, classNode, bytecode);

        return jvmLoadedClass;
    }


    private static void generate(JVMLoadedClass loadedClass, ClassNode classNode,
                                 LoadedBytecode bytecode) {

        var interfaces = new ArrayList<Class>();
        var fields = new ArrayList<Field>();
        var methods = new ArrayList<Method>();
        var access = classNode.access;
        Class superType;

        if (classNode.superName != null) {
            var type = getType(org.objectweb.asm.Type.getObjectType(classNode.superName), bytecode);
            if (!(type instanceof Class classType)) {
                throw new RuntimeException("superType is not a class");
            }
            superType = classType;
        } else {
            superType = null;
        }


        for (var anInterface : classNode.interfaces) {
            var interfacePath = new ObjectPath(anInterface.replace("/", "."), ".");
            interfaces.add(asJVMClass(interfacePath, bytecode));
        }

        for (var fieldNode : classNode.fields) {
            var fieldType = getType(org.objectweb.asm.Type.getType(fieldNode.desc), bytecode);
            var field = new Field(fieldNode.access, fieldNode.name, fieldType, loadedClass);
            fields.add(field);
        }

        for (var methodNode : classNode.methods) {
            var argumentTypes = org.objectweb.asm.Type.getArgumentTypes(methodNode.desc);
            var returnType =
                    getType(org.objectweb.asm.Type.getReturnType(methodNode.desc), bytecode);
            var args = Arrays.stream(argumentTypes).map(ref -> getType(ref, bytecode)).toList();
            methods.add(
                    new Method(methodNode.access, methodNode.name, returnType, args, loadedClass));
        }

        loadedClass.setInterfaces(interfaces);
        loadedClass.setFields(fields);
        loadedClass.setMethods(methods);
        loadedClass.setAccess(access);
        loadedClass.setSuperClass(superType);

    }


    private static @Nullable InputStream getBytecode(ObjectPath path) {
        return ClassLoader.getSystemResourceAsStream(path.asJVMPath());
    }

    private static Type getType(org.objectweb.asm.Type type, LoadedBytecode bytecode) {
        return switch (type.getSort()) {
            case org.objectweb.asm.Type.VOID -> new VoidType();
            case org.objectweb.asm.Type.BOOLEAN ->
                    new PrimitiveType(PrimitiveType.APrimitiveType.BOOLEAN);
            case org.objectweb.asm.Type.CHAR ->
                    new PrimitiveType(PrimitiveType.APrimitiveType.CHAR);
            case org.objectweb.asm.Type.BYTE ->
                    new PrimitiveType(PrimitiveType.APrimitiveType.BYTE);
            case org.objectweb.asm.Type.SHORT ->
                    new PrimitiveType(PrimitiveType.APrimitiveType.SHORT);
            case org.objectweb.asm.Type.INT -> new PrimitiveType(PrimitiveType.APrimitiveType.INT);
            case org.objectweb.asm.Type.FLOAT ->
                    new PrimitiveType(PrimitiveType.APrimitiveType.FLOAT);
            case org.objectweb.asm.Type.LONG ->
                    new PrimitiveType(PrimitiveType.APrimitiveType.LONG);
            case org.objectweb.asm.Type.DOUBLE ->
                    new PrimitiveType(PrimitiveType.APrimitiveType.DOUBLE);
            case org.objectweb.asm.Type.ARRAY ->
                    new ArrayType(getType(type.getElementType(), bytecode));
            case org.objectweb.asm.Type.OBJECT ->
                    asJVMClass(new ObjectPath(type.getClassName(), "."), bytecode);
            default -> {
                throw new NullPointerException("cant fetch type");
            }
        };
    }


}
