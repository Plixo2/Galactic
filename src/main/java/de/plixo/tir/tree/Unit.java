package de.plixo.tir.tree;

import de.plixo.hir.expr.HIRExpr;
import de.plixo.hir.item.HIRConst;
import de.plixo.hir.item.HIRImport;
import de.plixo.hir.item.HIRItem;
import de.plixo.hir.item.HIRStruct;
import de.plixo.tir.expr.Expr;
import de.plixo.tir.expr.PathExpr;
import de.plixo.typesys.types.GenericType;
import de.plixo.typesys.types.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Unit {

    @Getter
    private final Package parent;

    @Getter
    private final String localName;

    private final Map<String, Structure> structures = new LinkedHashMap<>();
    private final Map<String, Constant> constants = new LinkedHashMap<>();

    private final List<Import> imports = new ArrayList<>();

    //constants and imports
    private List<HIRItem> todo = new ArrayList<>();

    public Unit(Package parent, String localName) {
        this.parent = parent;
        this.localName = localName;
    }

    public String absolutName() {
        if (parent == null) {
            return localName;
        }
        return parent.absolutName() + "." + localName;
    }

    public List<HIRImport> getAndRemoveImports() {
        var constants = new ArrayList<HIRItem>();
        var imports = new ArrayList<HIRImport>();
        for (var hirItem : todo) {
            if (hirItem instanceof HIRImport aImport) {
                imports.add(aImport);
            } else {
                constants.add(hirItem);
            }
        }
        this.todo = constants;
        return imports;
    }

    public List<HIRConst> getAndRemoveConstants() {
        var imports = new ArrayList<HIRItem>();
        var constants = new ArrayList<HIRConst>();
        for (var hirItem : todo) {
            if (hirItem instanceof HIRConst aImport) {
                constants.add(aImport);
            } else {
                imports.add(hirItem);
            }
        }
        this.todo = imports;
        return constants;
    }

    public void addTodo(HIRItem item) {
        if (item instanceof HIRStruct) {
            throw new NullPointerException("Invalid todo item");
        } else {
            todo.add(item);
        }
    }

    public void addStructure(Structure structure) {
        if (doesContain(structure)) {
            throw new NullPointerException("todo message");
        }
        structures.put(structure.localName, structure);
    }

    public void addConstant(Constant structure) {
        if (doesContain(structure)) {
            throw new NullPointerException("todo message");
        }
        constants.put(structure.localName, structure);
    }

    public void addImport(Import aImport) {
        if (doesContain(aImport)) {
            throw new NullPointerException("todo message");
        }
        imports.add(aImport);
    }

    public boolean doesContain(Import aImport) {
        return imports.contains(aImport);
    }

    public boolean doesContain(Structure structure) {
        return structures.containsKey(structure.localName);
    }

    public boolean doesContain(Constant constant) {
        return constants.containsKey(constant.localName);
    }

    public List<Structure> structs() {
        return new ArrayList<>(structures.values());
    }

    public List<Constant> constants() {
        return new ArrayList<>(constants.values());
    }

    public @Nullable Structure getStructure(String name) {
        return structures.get(name);
    }

    public @Nullable Constant getConstant(String name) {
        return constants.get(name);
    }

    @Override
    public String toString() {
        return "Unit " + absolutName();
    }

    public List<Import> imports() {
        return new ArrayList<>(imports);
    }


    public @Nullable PathExpr findPath(String path) {
        for (Import aimport : this.imports) {
            var structure = aimport.findPath(path);
            if (structure != null) {
                return structure;
            }
        }
        return null;
    }
    public @Nullable Unit.Structure findImport(List<String> path) {
        for (Import aimport : this.imports) {
            var structure = aimport.findStructure(path);
            if (structure != null) {
                return structure;
            }
        }
        var queue = new ArrayDeque<>(path);
        var root = parent.root();
        while (!queue.isEmpty()) {
            var name = queue.poll();
            var aPackage = root.getPackage(name);
            if (aPackage == null) {
                var unit = root.getUnit(name);
                if (unit == null) {
                    return null;
                }
                return Import.findStructure(unit,queue,false);
            } else {
                root = aPackage;
            }
        }
        return null;
    }
    public @Nullable Unit.Constant findConstant(String path) {
        for (Import aimport : this.imports) {
            var constant = aimport.findConstant(path);
            if (constant != null) {
                return constant;
            }
        }
        return null;
    }


    public static class Structure {

        @Getter
        private final Unit unit;

        @Getter
        private final String localName;
        List<GenericType> generics = new ArrayList<>(); //to be filled in

        Map<String, Type> fields = new LinkedHashMap<>(); //to be filled in

        @Getter
        private @Nullable HIRStruct todo = null;


        public Structure(Unit unit, String localName) {
            this.unit = unit;
            this.localName = localName;
        }

        public void addTodo(HIRStruct struct) {
            this.todo = struct;
        }

        public void addGenerics(String name) {
            generics.add(new GenericType(name));
        }

        public String absolutName() {
            return unit.absolutName() + "." + localName;
        }

        public @Nullable Type getField(String name) {
            return fields.get(name);
        }

        public List<GenericType> generics() {
            return new ArrayList<>(generics);
        }

        public Map<String, Type> fields() {
            return new LinkedHashMap<>(fields);
        }

        public void addField(String name, Type type) {
            this.fields.put(name,type);
        }
    }

    public static class Constant {
        @Getter
        private final Unit unit;

        @Getter
        private final String localName;
        @Getter
        private final Type type;

        @Getter
        private final HIRExpr todo;

        @Getter
        @Setter
        @Accessors(fluent = false)
        private @Nullable Expr expr = null;


        public Constant(Unit unit, String localName, Type type, HIRExpr todo) {
            this.unit = unit;
            this.localName = localName;
            this.type = type;
            this.todo = todo;
        }

        public String absolutName() {
            return unit.absolutName() + "." + localName;
        }
    }
}
