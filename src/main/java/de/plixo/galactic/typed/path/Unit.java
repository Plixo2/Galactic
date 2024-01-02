package de.plixo.galactic.typed.path;

import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.files.FileTreeEntry;
import de.plixo.galactic.files.ObjectPath;
import de.plixo.galactic.high_level.items.HIRItem;
import de.plixo.galactic.lexer.Position;
import de.plixo.galactic.lexer.Region;
import de.plixo.galactic.typed.Context;
import de.plixo.galactic.typed.MethodCollection;
import de.plixo.galactic.typed.expressions.Expression;
import de.plixo.galactic.typed.expressions.StaticClassExpression;
import de.plixo.galactic.typed.expressions.StaticMethodExpression;
import de.plixo.galactic.typed.stellaclass.MethodOwner;
import de.plixo.galactic.typed.stellaclass.StellaBlock;
import de.plixo.galactic.typed.stellaclass.StellaClass;
import de.plixo.galactic.typed.stellaclass.StellaMethod;
import de.plixo.galactic.types.Class;
import de.plixo.galactic.types.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Unit of code (a file).
 * Has code-blocks (for debugging only), classes, imports and static methods
 */
@RequiredArgsConstructor
@Getter
public final class Unit implements CompileRoot {
    @Nullable
    final Package parent;
    final String localName;

    private final FileTreeEntry.FileTreeUnit treeUnit;

    private final List<StellaClass> classes = new ArrayList<>();

    private final List<Import> imports = new ArrayList<>();

    private final List<StellaMethod> staticMethods = new ArrayList<>();

    private List<HIRItem> hirItems = new ArrayList<>();

    public void addItem(HIRItem item) {
        hirItems.add(item);
    }

    public void addStaticMethod(StellaMethod method) {
        staticMethods.add(method);
    }


    public void addClass(StellaClass stellaClass) {
        this.classes.add(stellaClass);
    }


    @Override
    public String name() {
        if (parent == null) {
            return localName;
        }
        return STR."\{parent.name()}.\{localName}";
    }

    @Override
    public ObjectPath toObjectPath() {
        if (parent == null) {
            return new ObjectPath(localName);
        }
        return parent.toObjectPath().add(localName);
    }

    @Override
    public List<Unit> getUnits() {
        return List.of(this);
    }

    @Override
    public PathElement toPathElement() {
        return new PathElement.UnitElement(this);
    }

    public @Nullable Class getImportedClass(String name) {
        for (var anImport : imports) {
            if (anImport.alias().equals(name)) {
                if (anImport instanceof Import.ClassImport classImport) {
                    return classImport.importedClass();
                }
            }
        }
        return null;
    }

    public @Nullable StellaMethod getImportedStaticMethod(String name) {
        for (var anImport : imports) {
            if (anImport.alias().equals(name)) {
                if (anImport instanceof Import.StaticMethodImport methodImport) {
                    return methodImport.method();
                }
            }
        }
        return null;
    }

    public @Nullable Expression getDotNotation(Region region, String name) {
        for (var aClass : classes) {
            if (aClass.localName().equals(name)) {
                return new StaticClassExpression(region, aClass);
            }
        }
        var methods = staticMethods.stream().map(StellaMethod::asMethod)
                .filter(ref -> ref.name().equals(name)).toList();
        var methodCollection = new MethodCollection(name, methods);
        methodCollection = methodCollection.filter(Method::isStatic);
        if (!methodCollection.isEmpty()) {
            return new StaticMethodExpression(region, new MethodOwner.UnitOwner(this),
                    methodCollection);
        }
        return null;
    }

    public @Nullable Class locateClass(ObjectPath path, Context context, boolean useImports) {
        if (path.names().size() == 1 && useImports) {
            var name = path.names().getFirst();
            for (var anImport : imports) {
                if (anImport.alias().equals(name)) {
                    if (anImport instanceof Import.ClassImport classImport) {
                        return classImport.importedClass();
                    }
                }
            }
        }
        var stellaClass = locateStellaClass(path, context);
        if (stellaClass != null) {
            return stellaClass;
        }
        return locateJVMClass(path, context.loadedBytecode());
    }


    public @Nullable Class locateJVMClass(ObjectPath objectPath, LoadedBytecode bytecode) {
        return JVMLoader.asJVMClass(objectPath, bytecode);
    }

    public @Nullable StellaClass locateStellaClass(ObjectPath path, Context context) {
        var element = context.root().toPathElement().get(path);
        if (element instanceof PathElement.StellaClassElement(var aClass)) {
            return aClass;
        }
        return null;
    }


    public void addImport(Region region, String name, Class aClass, boolean isUserDefined) {
        imports.add(new Import.ClassImport(region, name, aClass, isUserDefined));
    }

    public void addImport(Region region, String name, StellaMethod method, boolean isUserDefined) {
        assert method.isStatic();
        imports.add(new Import.StaticMethodImport(region, name, method, isUserDefined));
    }

    public String getJVMDestination() {
        return this.toObjectPath().asSlashString();
    }

    public void clearImports() {
        imports.clear();
    }

    public Region getRegion() {
       return new Position(this.treeUnit().file(), 0, 0).toRegion();
    }
}
