package de.plixo.galactic.tir.stellaclass;

import de.plixo.galactic.hir.items.HIRClass;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.MethodCollection;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.tir.path.Unit;
import de.plixo.galactic.tir.stellaclass.method.AbstractMethod;
import de.plixo.galactic.tir.stellaclass.method.ImplementedMethod;
import de.plixo.galactic.tir.stellaclass.method.MethodImplementation;
import de.plixo.galactic.tir.stellaclass.method.NewMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.*;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * Represents a class inside a Unit
 */
public class StellaClass extends Class {
    @Getter
    private final String localName;
    @Getter
    private final Unit unit;
    @Getter
    private final HIRClass hirClass;
    public Class superClass;

    public StellaClass(String localName, Unit unit, HIRClass hirClass, Class defaultSuperClass) {
        this.localName = localName;
        this.unit = unit;
        this.hirClass = hirClass;
        this.superClass = defaultSuperClass;
    }

    public List<Class> interfaces = new ArrayList<>();
    public List<Field> fields = new ArrayList<>();

    @Getter
    private final List<MethodImplementation> methods = new ArrayList<>();

    public void addMethod(StellaMethod method, Context context) {
        if (method.isAbstract()) {
            methods.add(new AbstractMethod(method));
            return;
        }
        var aMethod = method.asMethod();
        for (var abstractMethod : superClass.getMethods()) {
            if (signatureMatch(abstractMethod, aMethod, context)) {
                methods.add(new ImplementedMethod(abstractMethod, method));
                return;
            }
        }
        for (Class anInterface : interfaces) {
            for (var abstractMethod : anInterface.getMethods()) {
                if (signatureMatch(abstractMethod, aMethod, context)) {
                    methods.add(new ImplementedMethod(abstractMethod, method));
                    return;
                }
            }
        }
        methods.add(new NewMethod(method));
    }

    private boolean signatureMatch(Method method, Method aticMethod, Context context) {
        if (!method.name().equals(aticMethod.name())) {
            return false;
        }
        if (method.arguments().size() != aticMethod.arguments().size()) {
            return false;
        }
        for (int i = 0; i < method.arguments().size(); i++) {
            var aType = method.arguments().get(i);
            var aticSide = aticMethod.arguments().get(i);
            if (!Type.isSame(aType, aticSide)) {
                return false;
            }
        }
        return Type.isSame(method.returnType(), aticMethod.returnType());
    }

    @Override
    public ObjectPath path() {
        return unit.toObjectPath().add(localName);
    }

    @Override
    public boolean isInterface() {
        return false;
    }


    @Override
    public List<Method> getAbstractMethods() {
        return methods.stream().filter(ref -> ref instanceof AbstractMethod)
                .map(MethodImplementation::asMethod).toList();
    }

    @Override
    public List<Method> getMethods() {
        var list = new ArrayList<Method>();
        list.addAll(methods.stream().map(MethodImplementation::asMethod).toList());
        list.addAll(superClass.getMethods());
        for (var anInterface : interfaces) {
            list.addAll(anInterface.getMethods());
        }

        return list;
    }

    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public @Nullable Field getField(String name, Context context) {
        for (var field : fields) {
            if (field.name().equals(name)) {
                return field;
            }
        }
        return superClass.getField(name, context);
    }

    @Override
    public MethodCollection getMethods(String name, Context context) {
        var aMethods = methods.stream().map(MethodImplementation::asMethod)
                .filter(ref -> ref.name().equals(name)).toList();
        var methods = new MethodCollection(name, aMethods);
        if (superClass != null) {
            methods = methods.join(superClass.getMethods(name, context));
        }

        return methods;
    }

    @Override
    public @Nullable Class getSuperClass() {
        return superClass;
    }

    @Override
    public List<Class> getInterfaces() {
        return interfaces;
    }


    public void addAllFieldsConstructor(Context context) {
        var params = new ArrayList<Parameter>();
        for (var field : this.fields) {
            params.add(new Parameter(field.name(), field.type()));
        }
        var owner = new MethodOwner.ClassOwner(this);
        addMethod(new StellaMethod(ACC_PUBLIC, "<init>", params, new VoidType(), null, owner),
                context);
    }

    public Set<Method> implementationLeft() {
        var set = new HashSet<>(superClass.getAbstractMethods());
        for (var anInterface : interfaces) {
            set.addAll(anInterface.getAbstractMethods());
        }
        for (MethodImplementation method : methods) {
            if (method instanceof ImplementedMethod implemented) {
                set.remove(implemented.toImplement());
            }
        }

        return set;
    }

    public String name() {
        return unit().name() + "." + localName();
    }

    @Override
    public ClassSource getSource() {
        return new ClassSource.AticSource(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StellaClass stellaClass = (StellaClass) o;
        return Objects.equals(this.path(), stellaClass.path());
    }

    @Override
    public int hashCode() {
        return Objects.hash(path());
    }
}

