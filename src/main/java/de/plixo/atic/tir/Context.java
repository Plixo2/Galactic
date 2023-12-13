package de.plixo.atic.tir;

import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.PathElement;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.Class;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable Class locateImported(String name) {
        return unit.locateImported(name);
    }

    @SneakyThrows
    public @Nullable Class getClass(ObjectPath objectPath) {
        return unit.locateClass(objectPath, this, true);
    }

}
