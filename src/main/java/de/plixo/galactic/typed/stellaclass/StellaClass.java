package de.plixo.galactic.typed.stellaclass;

import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.high_level.expressions.*;
import de.plixo.galactic.high_level.items.HIRClass;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.typed.stellaclass.method.AbstractMethod;
import de.plixo.galactic.typed.stellaclass.method.ImplementedMethod;
import de.plixo.galactic.typed.stellaclass.method.MethodImplementation;
import de.plixo.galactic.typed.stellaclass.method.NewMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.*;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * Represents a class inside a Unit
 */
public class StellaClass extends Class {
    @Getter
    private final Region region;
    @Getter
    private final String localName;
    @Getter
    private final Unit unit;
    @Getter
    private @Nullable
    final HIRClass hirClass;
    public Class superClass;

    public StellaClass(Region region, String localName, Unit unit, @Nullable HIRClass hirClass,
                       Class defaultSuperClass) {
        this.region = region;
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
            if (Method.signatureMatch(abstractMethod, aMethod, context)) {
                methods.add(new ImplementedMethod(abstractMethod, method));
                return;
            }
        }
        for (Class anInterface : interfaces) {
            for (var abstractMethod : anInterface.getMethods()) {
                if (Method.signatureMatch(abstractMethod, aMethod, context)) {
                    methods.add(new ImplementedMethod(abstractMethod, method));
                    return;
                }
            }
        }
        methods.add(new NewMethod(method));
    }


    @Override
    public ObjectPath path() {
        return unit.toObjectPath().add(localName);
    }

    @Override
    public int modifiers() {
        return ACC_PUBLIC;
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
    public @Nullable Class getSuperClass() {
        return superClass;
    }

    @Override
    public List<Class> getInterfaces() {
        return interfaces;
    }


    public void addAllFieldsConstructor(Context context) {
        var expressions = new ArrayList<HIRExpression>();
        var params = new ArrayList<Parameter>();
        var thisSymbol = new HIRThis(region());
        for (var field : this.fields) {
            params.add(new Parameter(field.name(), field.type()));
            var dotNotation = new HIRDotNotation(region(), thisSymbol, field.name());
            var valueSymbol = new HIRIdentifier(region(), field.name());
            var assign = new HIRAssign(region(), dotNotation, valueSymbol);
            expressions.add(assign);
        }
        var owner = new MethodOwner.ClassOwner(this);
        var method = new StellaMethod(ACC_PUBLIC, "<init>", params, new VoidType(),
                new HIRBlock(region(), expressions), owner, null, region());
        method.thisContext(this);

        addMethod(method, context);
    }


    public String name() {
        return STR."\{unit().name()}.\{localName()}";
    }

    @Override
    public ClassSource getSource() {
        return new ClassSource.StellaSource(this);
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

