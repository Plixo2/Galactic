package de.plixo.galactic.tir.path;

import de.plixo.galactic.boundary.JVMLoader;
import de.plixo.galactic.boundary.LoadedBytecode;
import de.plixo.galactic.files.FileTreeEntry;
import de.plixo.galactic.hir.items.HIRItem;
import de.plixo.galactic.tir.Context;
import de.plixo.galactic.tir.Import;
import de.plixo.galactic.tir.MethodCollection;
import de.plixo.galactic.tir.ObjectPath;
import de.plixo.galactic.tir.stellaclass.StellaBlock;
import de.plixo.galactic.tir.stellaclass.StellaClass;
import de.plixo.galactic.tir.stellaclass.StellaMethod;
import de.plixo.galactic.tir.stellaclass.MethodOwner;
import de.plixo.galactic.tir.expressions.Expression;
import de.plixo.galactic.tir.expressions.StaticClassExpression;
import de.plixo.galactic.tir.expressions.StaticMethodExpression;
import de.plixo.galactic.types.Class;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Unit of code (a file)
 * Has code-blocks, classes, imports and static methods
 */
@RequiredArgsConstructor
@Getter
public final class Unit implements CompileRoot {
    @Nullable
    final Package parent;
    final String localName;

    private final FileTreeEntry.FileTreeUnit treeUnit;

    private final List<StellaClass> classes = new ArrayList<>();

    private final List<StellaBlock> blocks = new ArrayList<>();

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

    public void addBlock(StellaBlock block) {
        this.blocks.add(block);
    }

    @Override
    public String name() {
        if (parent == null) {
            return localName;
        }
        return parent.name() + "." + localName;
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
                return anImport.importedClass();
            }
        }
        return null;
    }

    public @Nullable Expression getDotNotation(String name) {
        for (var aClass : classes) {
            if (aClass.localName().equals(name)) {
                return new StaticClassExpression(aClass);
            }
        }
        var methods = staticMethods.stream().map(StellaMethod::asMethod)
                .filter(ref -> ref.name().equals(name)).toList();
        var methodCollection = new MethodCollection(name, methods);
        if (!methodCollection.isEmpty()) {
            return new StaticMethodExpression(new MethodOwner.UnitOwner(this), methodCollection);
        }
        return null;
    }

    public @Nullable Class locateClass(ObjectPath path, Context context, boolean useImports) {
        if (path.names().size() == 1 && useImports) {
            var name = path.names().get(0);
            for (var anImport : imports) {
                if (anImport.alias().equals(name)) {
                    return anImport.importedClass();
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


    public void addImport(String name, Class aClass) {
        imports.add(new Import(name, aClass));
    }

    public String getJVMDestination() {
        return this.toObjectPath().asSlashString();
    }
}
