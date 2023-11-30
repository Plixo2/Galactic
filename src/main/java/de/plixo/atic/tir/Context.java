package de.plixo.atic.tir;

import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.PathElement;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.AClass;
import de.plixo.atic.types.AType;
import de.plixo.atic.types.AVoid;
import de.plixo.atic.types.classes.JVMClass;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.util.Objects;
import java.util.Stack;

public class Context {

    private final Unit unit;

    private final Stack<Scope> scopes = new Stack<>();

    public Context(Unit unit, CompileRoot root) {
        this.unit = unit;
        this.root = root;
        pushScope(new Scope(null));
    }

    @Getter
    private final CompileRoot root;

    public void pushScope(Scope scope) {
        scopes.push(scope);
    }

    public void pushScope() {
        scopes.push(new Scope(this.scope()));
    }

    public void popScope() {
        scopes.pop();
    }

    public Scope scope() {
        return scopes.peek();
    }


    public @Nullable PathElement locate(String name) {
        return unit.locate(name);
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
