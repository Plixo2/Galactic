package de.plixo.atic.tir;

import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.classes.JVMClass;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Context {

    private @Nullable Context parent;
    private final List<Variable> variables = new ArrayList<>();
    private final Unit unit;

//    @Getter
//    private final AticRegister aticRegister;

    @Getter
    private final CompileRoot root;

    public Variable addVariable(String name, AType type, VariableType variableType) {
        var variable = new Variable(name, type, variableType);
        variables.add(variable);
        return variable;
    }

    public @Nullable Variable getVariable(String id) {
        for (var variable : variables) {
            if (variable.name.equals(id)) {
                return variable;
            }
        }
        if (parent == null) {
            return null;
        }
        return parent.getVariable(id);

    }

    @SneakyThrows
    public @Nullable AClass getClass(ObjectPath objectPath) {
        var stream = Context.class.getResourceAsStream(objectPath.asJVMPath());
        if (stream != null) {
            ClassNode cn = new ClassNode();
            ClassReader cr = new ClassReader(stream);
            cr.accept(cn, 0);
            return new JVMClass(cn.name);
        }
//        var path = ByteCodeMemo.getPath(objectPath);
//        if (path != null) {
//            return new JVMClass(path.name);
//        }
        return unit.locateClass(objectPath, this);
        // var aClass = aticRegister.getClass(objectPath);
        // return aClass;
        //throw new NullPointerException("not supported " + objectPath);
    }


    public Context childContext() {
        return new Context(this, unit, root);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Variable {
        @Getter
        private final String name;
        @Getter
        private final AType type;
        @Getter
        private final VariableType variableType;
    }

    public enum VariableType {
        INPUT,
        LOCAL
    }


}
