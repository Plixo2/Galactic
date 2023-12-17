package de.plixo.atic.tir;

import de.plixo.atic.boundary.LoadedBytecode;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.MethodOwner;
import de.plixo.atic.tir.expressions.*;
import de.plixo.atic.tir.path.CompileRoot;
import de.plixo.atic.tir.path.Package;
import de.plixo.atic.tir.path.Unit;
import de.plixo.atic.types.Class;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

/**
 * Context for the compiler, including scopes and the current unit.
 * Subtypes are can be used to add additional information to the compiler, like type hints.
 */
public class Context {

    private final Unit unit;
    @Getter
    private final CompileRoot root;

    @Getter
    private final LoadedBytecode loadedBytecode;

    private final Stack<Scope> scopes = new Stack<>();

    public Context(Unit unit, CompileRoot root, LoadedBytecode loadedBytecode) {
        this.unit = unit;
        this.root = root;
        this.loadedBytecode = loadedBytecode;
        pushScope(new Scope(null));
    }


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


    /**
     * Make a Expression from a Name, as a top level name
     *
     * @param name name of the Symbol
     * @return
     */
    public @Nullable Expression getSymbolExpression(String symbol, Context context) {
        var variable = scope().getVariable(symbol);
        if (variable != null) {
            return new VarExpression(variable);
        }
        var aClass = unit.getImportedClass(symbol);
        if (aClass != null) {
            return new StaticClassExpression(aClass);
        }
        var methods = unit.staticMethods().stream().map(AticMethod::asAMethod)
                .filter(ref -> ref.name().equals(symbol)).toList();
        var methodCollection = new MethodCollection(symbol, methods);
        if (!methodCollection.isEmpty()) {
            return new StaticMethodExpression(new MethodOwner.UnitOwner(unit), methodCollection);
        }
        if (context.root().name().equals(symbol)) {
            switch (context.root()) {
                case Package aPackage -> {
                    return new AticPackageExpression(aPackage);
                }
                case Unit unit1 -> {
                    return new UnitExpression(unit1);
                }
            }
        }

        return null;
    }

    public @Nullable Class getClass(ObjectPath objectPath) {
        return unit.locateClass(objectPath, this, true);
    }

}
