package de.plixo.galactic.boundary;

import de.plixo.galactic.exception.FlairException;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.files.ObjectPath;
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
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a class loaded into the JVM
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = false)
@Setter
public class JVMLoadedClass extends Class {
    private final LoadedBytecode bytecode;
    @Getter
    private final ClassNode node;
    @Getter
    private final byte[] data;
    private final ObjectPath path;
    private final String name;

    private String superClassName = null;
    private @Nullable Class superClass = null;

    private List<String> interfaceNames = new ArrayList<>();
    private List<Class> interfaces = new ArrayList<>();

    private List<MethodNode> methodNodes = new ArrayList<>();
    private List<Method> methods = new ArrayList<>();

    private List<FieldNode> fieldNodes = new ArrayList<>();
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

    public int modifiers() {
        return node.access;
    }

    @Override
    public List<Method> getAbstractMethods() {
        return evaluateMethods().stream().filter(Method::isAbstract).toList();
    }

    @Override
    public List<Method> getMethods() {
        var list = new ArrayList<>(evaluateMethods());
        var evaluateSuperClass = evaluateSuperClass();
        if (evaluateSuperClass != null) {
            list.addAll(evaluateSuperClass.getMethods());
        }
        for (var anInterface : evaluateInterfaces()) {
            list.addAll(anInterface.getMethods());
        }
        return list;
    }

    @Override
    public List<Field> getFields() {
        var list = new ArrayList<>(evaluateFields());
        var evaluateSuperClass = evaluateSuperClass();
        if (evaluateSuperClass != null) {
            list.addAll(evaluateSuperClass.getFields());
        }
        //unnecessary, interfaces cant have fields, in theories for now at least
        for (var anInterface : evaluateInterfaces()) {
            list.addAll(anInterface.getFields());
        }
        //TODO check collisions in check stage
        return list;
    }


    @Override
    public @Nullable Class getSuperClass() {
        return evaluateSuperClass();
    }

    @Override
    public List<Class> getInterfaces() {
        return evaluateInterfaces();
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

    public List<Method> evaluateMethods() {
        for (MethodNode methodNode : methodNodes) {
            var argumentTypes = org.objectweb.asm.Type.getArgumentTypes(methodNode.desc);
            var returnType =
                    JVMLoader.getType(org.objectweb.asm.Type.getReturnType(methodNode.desc),
                            bytecode);
            var args = Arrays.stream(argumentTypes).map(ref -> JVMLoader.getType(ref, bytecode))
                    .toList();
            methods.add(new Method(methodNode.access, methodNode.name, returnType, args,
                    new MethodOwner.ClassOwner(this)));
        }
        methodNodes = new ArrayList<>();
        return methods;
    }

    public List<Field> evaluateFields() {
        for (FieldNode fieldNode : fieldNodes) {
            var fieldType =
                    JVMLoader.getType(org.objectweb.asm.Type.getType(fieldNode.desc), bytecode);
            var field = new Field(fieldNode.access, fieldNode.name, fieldType, this);
            fields.add(field);
        }
        fieldNodes = new ArrayList<>();
        return fields;
    }

    public @Nullable Class evaluateSuperClass() {
        if (superClassName != null) {
            var type = JVMLoader.getType(org.objectweb.asm.Type.getObjectType(superClassName),
                    bytecode);
            if (!(type instanceof Class classType)) {
                throw new FlairException(STR."super class is not a class of \{this.name()}");
            }
            superClass = classType;
        }
        superClassName = null;
        return superClass;
    }

    public List<Class> evaluateInterfaces() {
        for (var anInterface : interfaceNames) {
            var interfacePath = new ObjectPath(anInterface.replace("/", "."), ".");
            interfaces.add(JVMLoader.asJVMClass(interfacePath, bytecode));
        }
        interfaceNames = new ArrayList<>();
        return interfaces;
    }


}
