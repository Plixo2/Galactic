package de.plixo.tir.tree;

import de.plixo.tir.expr.PathExpr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public abstract sealed class Import
        permits Import.StructureImport, Import.UnitImport, Import.ConstantImport {

    abstract String localName();

    public @Nullable Unit.Constant findConstant(String path) {
        if (this instanceof Import.ConstantImport constantImport) {
            if (constantImport.constant.localName().equals(path)) {
                return constantImport.constant;
            }
        }
        return null;
    }

    public @Nullable PathExpr findPath(String path) {
        if (this.localName().equals(path)) {
            return switch (this) {
                case StructureImport structureImport ->
                        new PathExpr.StructPathExpr(structureImport.structure);
                case UnitImport unitImport -> new PathExpr.UnitPathExpr(unitImport.unit);
                case ConstantImport ignored -> null;
            };
        }
        return null;
    }

    public final @Nullable Unit.Structure findStructure(List<String> path) {
        var queue = new ArrayDeque<>(path);
        if (queue.isEmpty()) {
            return null;
        }
        return switch (this) {
            case StructureImport structureImport -> findStructure(structureImport.structure, queue);
            case UnitImport unitImport -> findStructure(unitImport.unit, queue, true);
            case ConstantImport ignored -> null;
        };
    }

    public static @Nullable Unit.Structure findStructure(Unit.Structure structure,
                                                         Queue<String> queue) {
        var structName = queue.poll();
        if (!structure.localName().equals(structName)) {
            return null;
        }
        if (!queue.isEmpty()) {
            //or error?
            return null;
        }
        return structure;
    }

    public static @Nullable Unit.Structure findStructure(Unit unit, Queue<String> queue,
                                                         boolean testTopName) {

        if (testTopName) {
            var unitName = queue.poll();
            if (!unit.localName().equals(unitName)) {
                return null;
            }
        }
        if (queue.isEmpty()) {
            return null;
        }
        var structName = queue.poll();
        var structure = unit.getStructure(structName);
        if (structure == null) {
            //or error?
            return null;
        }
        if (!queue.isEmpty()) {
            //or error?
            return null;
        }
        return structure;
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

        @Override
        String localName() {
            return unit.localName();
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

        @Override
        String localName() {
            return structure.localName();
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

        @Override
        String localName() {
            return constant.localName();
        }
    }


}
