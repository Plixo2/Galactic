package de.plixo.atic.tir.aticclass;

import de.plixo.atic.hir.items.HIRClass;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.aticclass.method.AbstractMethod;
import de.plixo.atic.tir.aticclass.method.ImplementedMethod;
import de.plixo.atic.tir.aticclass.method.MethodImplementation;
import de.plixo.atic.tir.aticclass.method.NewMethod;
import de.plixo.atic.tir.path.PathElement;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.Class;
import de.plixo.atic.types.Type;
import de.plixo.atic.types.VoidType;
import de.plixo.atic.types.JVMClass;
import de.plixo.atic.types.sub.Field;
import de.plixo.atic.types.sub.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

@RequiredArgsConstructor
public class AticClass extends Class implements PathElement {
    @Getter
    private final String localName;
    @Getter
    private final Unit unit;
    @Getter
    private final HIRClass hirClass;
    public Class superClass = new JVMClass("java.lang.Object");
    public List<Class> interfaces = new ArrayList<>();
    public List<Field> fields = new ArrayList<>();

    @Getter
    private final List<MethodImplementation> methods = new ArrayList<>();

    public void addMethod(AticMethod method, Context context) {
        if (method.isAbstract()) {
            methods.add(new AbstractMethod(method));
            return;
        }
        var aMethod = method.asAMethod();
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
                .filter(ref -> ref.name().equals(name))
//                .filter(ref -> Modifier.isPublic(ref.modifier()))
                .toList();
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
//        System.out.println(methods.size());
        addMethod(new AticMethod(this, ACC_PUBLIC, "<init>", params, new VoidType(), null), context);
//        System.out.println(methods.size());
    }

    public Set<Method> implementationLeft() {
        var set = new HashSet<Method>();
        set.addAll(superClass.getAbstractMethods());
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AticClass aticClass = (AticClass) o;
        return Objects.equals(this.path(), aticClass.path());
    }

    @Override
    public int hashCode() {
        return Objects.hash(path());
    }
}

