package de.plixo.atic.tir.path;

import de.plixo.atic.boundary.JVMLoader;
import de.plixo.atic.boundary.LoadedBytecode;
import de.plixo.atic.files.FileTreeEntry;
import de.plixo.atic.hir.items.HIRItem;
import de.plixo.atic.tir.Context;
import de.plixo.atic.tir.Import;
import de.plixo.atic.tir.ObjectPath;
import de.plixo.atic.tir.aticclass.AticBlock;
import de.plixo.atic.tir.aticclass.AticClass;
import de.plixo.atic.tir.aticclass.AticMethod;
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
public final class Unit implements CompileRoot, PathElement {
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


    public @Nullable Class locateImported(String name) {
        for (var anImport : imports) {
            if (anImport.alias().equals(name)) {
                return anImport.importedClass();
            }
        }
        return null;
    }

    @Override
    public @Nullable PathElement locate(String name) {
        for (var aClass : classes) {
            if (aClass.localName().equals(name)) {
                return aClass;
            }
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

        CompileRoot prev = context.root();
        var iterator = path.names().iterator();
        while (iterator.hasNext()) {
            var name = iterator.next();
            var located = prev.locate(name);
            switch (located) {
                case Unit unit -> {
                    prev = unit;
                }
                case Package aPackage -> {
                    prev = aPackage;
                }
                case AticClass aticClass -> {
                    if (iterator.hasNext()) {
                        return null;
                    }
                    return aticClass;
                }
                case null -> {
                    return null;
                }
                default -> throw new IllegalStateException("Unexpected value: " + located);
            }
        }
        return null;
    }


    public void addImport(String name, Class aticClass) {
        imports.add(new Import(name, aticClass));
    }
}
