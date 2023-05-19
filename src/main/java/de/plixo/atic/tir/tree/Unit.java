package de.plixo.atic.tir.tree;

import de.plixo.atic.hir.expr.HIRExpr;
import de.plixo.atic.hir.item.HIRConst;
import de.plixo.atic.hir.item.HIRImport;
import de.plixo.atic.hir.item.HIRItem;
import de.plixo.atic.hir.item.HIRStruct;
import de.plixo.atic.tir.expr.Expr;
import de.plixo.atic.tir.expr.PathExpr;
import de.plixo.atic.typing.types.GenericType;
import de.plixo.atic.typing.types.Type;
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
            throw new NullPointerException("structure does already exist");
        }
        structures.put(structure.localName, structure);
    }

    public void addConstant(Constant constant) {
        if (doesContain(constant)) {
            //constant.localName = String.valueOf(Math.random());
            throw new NullPointerException("constant already exists");
        }
        constants.put(constant.localName, constant);
    }

    public void addImport(Import aImport) {
        if (doesContain(aImport)) {
            throw new NullPointerException("import does already exist");
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
                return Import.findStructure(unit, queue, false);
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
        private final List<GenericType> generics = new ArrayList<>(); //to be filled in

        private final Map<String, Field> fields = new LinkedHashMap<>(); //to be filled in

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

        public @Nullable Field getField(String name) {
            return fields.get(name);
        }

        public List<GenericType> generics() {
            return new ArrayList<>(generics);
        }

        public Map<String, Field> fields() {
            return new LinkedHashMap<>(fields);
        }

        public void addField(Field field) {
            this.fields.put(field.name(), field);
        }

        public List<Field> uninitialized() {
            return this.fields.values().stream().filter(ref -> ref.expr == null).toList();
        }

        public static class Field {
            @Getter
            private final String name;

            @Getter
            private final Type type;

            @Getter
            @Setter
            @Accessors(fluent = false)
            private Expr expr;

            @Getter
            private @Nullable HIRExpr todo;

            public Field(String name, Type type, Expr expr, @Nullable HIRExpr todo) {
                this.name = name;
                this.type = type;
                this.expr = expr;
                this.todo = todo;
            }

            public void clearTodo() {
                this.todo = null;
            }
        }
    }

    public static class Constant {
        @Getter
        private final Unit unit;

        @Getter
        private String localName;
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
