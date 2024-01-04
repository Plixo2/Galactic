package de.plixo.galactic.typed;

import de.plixo.galactic.Universe;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.expressions.*;
import de.plixo.galactic.typed.path.CompileRoot;
import de.plixo.galactic.typed.path.PathElement;
import de.plixo.galactic.typed.path.Unit;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Type;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

/**
 * Context for the compiler, including scopes and the current unit.
 * Subtypes are can be used to add additional information to the compiler, like type hints.
 */
public class Context {

    @Getter
    private final Universe language;
    @Getter
    private final Unit unit;
    @Getter
    private final @Nullable Class owningClass;
    @Getter
    @Setter
    private @Nullable Type thisContext;
    @Getter
    private final CompileRoot root;

    @Getter
    private final LoadedBytecode loadedBytecode;

    private final Stack<Scope> scopes = new Stack<>();

    public Context(Universe language, Unit unit, @Nullable Class owningClass, CompileRoot root,
                   LoadedBytecode loadedBytecode) {
        this.language = language;
        this.unit = unit;
        this.owningClass = owningClass;
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
     * Make an Expression from a Name, as a top level name
     *
     * @param symbol name of the Symbol
     */
    public @Nullable Expression getSymbolExpression(Region region, String symbol, Context context) {
        var variable = scope().getVariable(symbol);
        if (variable != null) {
            variable.addUsage();
            return new VarExpression(region, variable);
        }
        var aClass = unit.getImportedClass(symbol);
        if (aClass != null) {
            return new StaticClassExpression(region, aClass);
        }
        var staticMethod = unit.getImportedStaticMethod(symbol);
        if (!staticMethod.isEmpty()) {
            var collection = new MethodCollection(symbol,
                    staticMethod.stream().map(StellaMethod::asMethod).toList());
            return new StaticMethodExpression(region, collection);
        }
        return switch (context.root().toPathElement().next(symbol)) {
            case PathElement.PackageElement(var aPackage) -> {
                yield new StellaPackageExpression(region, aPackage);
            }
            case PathElement.UnitElement(var unit) -> {
                yield new UnitExpression(region, unit);
            }
            case null, default -> {
                yield null;
            }
        };
    }

    public @Nullable Class getClass(ObjectPath objectPath) {
        return unit.locateClass(objectPath, this, true);
    }


}
