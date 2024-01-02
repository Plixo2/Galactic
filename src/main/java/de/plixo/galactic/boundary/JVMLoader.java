package de.plixo.galactic.boundary;

import com.google.common.io.ByteStreams;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.*;
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
        byte[] bytes;
        try {
            bytes = ByteStreams.toByteArray(stream);
            classReader = new ClassReader(bytes);
        } catch (IOException e) {
            throw new FlairException("Trouble loading JVM class", e);
        }
        classReader.accept(classNode, 0);

        var jvmLoadedClass = new JVMLoadedClass(bytecode, classNode, bytes, path, classNode.name);
        bytecode.putClass(path, jvmLoadedClass);
        generate(jvmLoadedClass, classNode, bytecode);

        return jvmLoadedClass;
    }


    private static void generate(JVMLoadedClass loadedClass, ClassNode classNode,
                                 LoadedBytecode bytecode) {

        var interfaces = new ArrayList<Class>();
        var fields = new ArrayList<Field>();
        var methods = new ArrayList<Method>();

        loadedClass.setSuperClassName(classNode.superName);
//        if (classNode.superName != null) {
//            var type = getType(org.objectweb.asm.Type.getObjectType(classNode.superName), bytecode);
//            if (!(type instanceof Class classType)) {
//                throw new FlairException(STR."super class is not a class of \{loadedClass.name()}");
//            }
//            superType = classType;
//        } else {
//            superType = null;
//        }
//
        loadedClass.setInterfaceNames(classNode.interfaces);
//        for (var anInterface : classNode.interfaces) {
//            var interfacePath = new ObjectPath(anInterface.replace("/", "."), ".");
//            interfaces.add(asJVMClass(interfacePath, bytecode));
//        }

        loadedClass.setFieldNodes(classNode.fields);
//        for (var fieldNode : classNode.fields) {
//            var fieldType = getType(org.objectweb.asm.Type.getType(fieldNode.desc), bytecode);
//            var field = new Field(fieldNode.access, fieldNode.name, fieldType, loadedClass);
//            fields.add(field);
//        }
        loadedClass.setMethodNodes(classNode.methods);
//        for (var methodNode : classNode.methods) {
//            var argumentTypes = org.objectweb.asm.Type.getArgumentTypes(methodNode.desc);
//            var returnType =
//                    getType(org.objectweb.asm.Type.getReturnType(methodNode.desc), bytecode);
//            var args = Arrays.stream(argumentTypes).map(ref -> getType(ref, bytecode)).toList();
//            methods.add(new Method(methodNode.access, methodNode.name, returnType, args,
//                    new MethodOwner.ClassOwner(loadedClass)));
//        }

    }


    private static @Nullable InputStream getBytecode(ObjectPath path) {
        return ClassLoader.getSystemResourceAsStream(path.asJVMPath());
    }

    public static Type getType(org.objectweb.asm.Type type, LoadedBytecode bytecode) {
        return switch (type.getSort()) {
            case org.objectweb.asm.Type.VOID -> new VoidType();
            case org.objectweb.asm.Type.BOOLEAN ->
                    new PrimitiveType(PrimitiveType.StellaPrimitiveType.BOOLEAN);
            case org.objectweb.asm.Type.CHAR ->
                    new PrimitiveType(PrimitiveType.StellaPrimitiveType.CHAR);
            case org.objectweb.asm.Type.BYTE ->
                    new PrimitiveType(PrimitiveType.StellaPrimitiveType.BYTE);
            case org.objectweb.asm.Type.SHORT ->
                    new PrimitiveType(PrimitiveType.StellaPrimitiveType.SHORT);
            case org.objectweb.asm.Type.INT ->
                    new PrimitiveType(PrimitiveType.StellaPrimitiveType.INT);
            case org.objectweb.asm.Type.FLOAT ->
                    new PrimitiveType(PrimitiveType.StellaPrimitiveType.FLOAT);
            case org.objectweb.asm.Type.LONG ->
                    new PrimitiveType(PrimitiveType.StellaPrimitiveType.LONG);
            case org.objectweb.asm.Type.DOUBLE ->
                    new PrimitiveType(PrimitiveType.StellaPrimitiveType.DOUBLE);
            case org.objectweb.asm.Type.ARRAY ->
                    new ArrayType(getType(type.getElementType(), bytecode));
            case org.objectweb.asm.Type.OBJECT ->
                    asJVMClass(new ObjectPath(type.getClassName(), "."), bytecode);
            default -> {
                throw new NullPointerException("Cant fetch type");
            }
        };
    }


}
