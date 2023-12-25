package de.plixo.galactic.tir;

import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.tir.expressions.*;
import de.plixo.galactic.tir.path.CompileRoot;
import de.plixo.galactic.tir.path.Package;
import de.plixo.galactic.tir.path.Unit;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
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
    public @Nullable Expression getSymbolExpression(Region region, String symbol, Context context) {
        var variable = scope().getVariable(symbol);
        if (variable != null) {
            return new VarExpression(region, variable);
        }
        var aClass = unit.getImportedClass(symbol);
        if (aClass != null) {
            return new StaticClassExpression(region, aClass);
        }
        var methods = unit.staticMethods().stream().map(StellaMethod::asMethod)
                .filter(ref -> ref.name().equals(symbol)).toList();
        var methodCollection = new MethodCollection(symbol, methods);
        if (!methodCollection.isEmpty()) {
            return new StaticMethodExpression(region, new MethodOwner.UnitOwner(unit),
                    methodCollection);
        }
        if (context.root().name().equals(symbol)) {
            switch (context.root()) {
                case Package aPackage -> {
                    return new StellaPackageExpression(region, aPackage);
                }
                case Unit unit1 -> {
                    return new UnitExpression(region, unit1);
                }
            }
        }

        return null;
    }

    public @Nullable Class getClass(ObjectPath objectPath) {
        return unit.locateClass(objectPath, this, true);
    }

}
