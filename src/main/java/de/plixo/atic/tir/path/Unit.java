package de.plixo.atic.tir.path;

import de.plixo.atic.boundary.JVMLoader;
import de.plixo.atic.boundary.LoadedBytecode;
import de.plixo.atic.files.FileTreeEntry;
import de.plixo.atic.hir.items.HIRItem;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.Import;
import de.plixo.atic.tir.MethodCollection;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.aticclass.AticBlock;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.aticclass.AticMethod;
import de.plixo.atic.tir.aticclass.MethodOwner;
import de.plixo.atic.tir.expressions.Expression;
import de.plixo.atic.tir.expressions.StaticClassExpression;
import de.plixo.atic.tir.expressions.StaticMethodExpression;
import de.plixo.atic.types.Class;
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

    private final List<AticClass> classes = new ArrayList<>();

    private final List<AticBlock> blocks = new ArrayList<>();

    private final List<Import> imports = new ArrayList<>();

    private final List<AticMethod> staticMethods = new ArrayList<>();

    private List<HIRItem> hirItems = new ArrayList<>();

    public void addItem(HIRItem item) {
        hirItems.add(item);
    }
    public void addStaticMethod(AticMethod method) {
        staticMethods.add(method);
    }



    public void addClass(AticClass aticClass) {
        this.classes.add(aticClass);
    }

    public void addBlock(AticBlock block) {
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
        var methods = staticMethods.stream().map(AticMethod::asAMethod)
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
        var aticClass = locateAticClass(path, context);
        if (aticClass != null) {
            return aticClass;
        }
        return locateJVMClass(path, context.loadedBytecode());
    }


    public @Nullable Class locateJVMClass(ObjectPath objectPath, LoadedBytecode bytecode) {
        return JVMLoader.asJVMClass(objectPath, bytecode);
    }

    public @Nullable AticClass locateAticClass(ObjectPath path, Context context) {
        var element = context.root().toPathElement().get(path);
        if (element instanceof PathElement.AticClassElement(var aticClass)) {
            return aticClass;
        }
        return null;
    }


    public void addImport(String name, Class aticClass) {
        imports.add(new Import(name, aticClass));
    }

    public String getJVMDestination() {
        return this.toObjectPath().asSlashString();
    }
}
