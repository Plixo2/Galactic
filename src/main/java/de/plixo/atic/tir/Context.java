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
import java.util.Stack;

@AllArgsConstructor
public class Context {

    private final Unit unit;

    private final Stack<Scope> scopes = new Stack<>();


    @Getter
    private final CompileRoot root;

    public void pushScope(Scope scope) {
        scopes.push(scope);
    }

    public void popScope() {
        scopes.pop();
    }

    public Scope scope() {
        return scopes.peek();
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
        return unit.locateClass(objectPath, this);
    }


}
