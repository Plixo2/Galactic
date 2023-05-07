package de.plixo.tir.tree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;

public abstract sealed class Import
        permits Import.StructureImport, Import.UnitImport, Import.ConstantImport {
    public @Nullable Unit.Constant findConstant(String path) {
        if (this instanceof Import.ConstantImport constantImport) {
            if (constantImport.constant.localName().equals(path)) {
                return constantImport.constant;
            }
        }
        return null;
    }

    public @Nullable Unit.Structure findStructure(List<String> path) {
        var queue = new ArrayDeque<>(path);
        if (queue.isEmpty()) {
            return null;
        }
        return switch (this) {
            case StructureImport structureImport -> {
                var structName = queue.poll();
                var structure = structureImport.structure;
                if (!structure.localName().equals(structName)) {
                    yield null;
                }
                if (!queue.isEmpty()) {
                    //or error?
                    yield null;
                }
                yield structure;
            }
            case UnitImport unitImport -> {
                var unitName = queue.poll();
                var unit = unitImport.unit;
                if (!unit.localName().equals(unitName)) {
                    yield null;
                }
                if (queue.isEmpty()) {
                    yield null;
                }
                var structName = queue.poll();
                var structure = unit.getStructure(structName);
                if (structure == null) {
                    //or error?
                    yield null;
                }
                if (!queue.isEmpty()) {
                    //or error?
                    yield null;
                }
                yield structure;
            }
            case ConstantImport constantImport -> null;
        };
    }

    @AllArgsConstructor
    public static final class UnitImport extends Import {
        @Getter
        private Unit unit;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnitImport that = (UnitImport) o;
            return Objects.equals(unit, that.unit);
        }

        @Override
        public int hashCode() {
            return Objects.hash(unit);
        }

        @Override
        public String toString() {
            return "Import Unit " + unit.absolutName();
        }
    }


    @AllArgsConstructor
    public final static class StructureImport extends Import {
        @Getter
        private Unit.Structure structure;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StructureImport that = (StructureImport) o;
            return Objects.equals(structure, that.structure);
        }

        @Override
        public int hashCode() {
            return Objects.hash(structure);
        }

        @Override
        public String toString() {
            return "Import Struct " + structure.absolutName();
        }
    }

    @AllArgsConstructor
    public final static class ConstantImport extends Import {
        @Getter
        private Unit.Constant constant;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConstantImport that = (ConstantImport) o;
            return Objects.equals(constant, that.constant);
        }

        @Override
        public int hashCode() {
            return Objects.hash(constant);
        }

        @Override
        public String toString() {
            return "Import Constant " + constant.absolutName();
        }
    }

}
