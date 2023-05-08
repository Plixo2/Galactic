package de.plixo.tir.expr;

import de.plixo.tir.tree.Package;
import de.plixo.tir.tree.Unit;
import de.plixo.typesys.types.Type;
import lombok.Getter;

public sealed class PathExpr implements Expr {


    @Override
    public Type getType() {
        return null;
    }

    @Override
    public void fillType() {

    }

    public static final class UnitPathExpr extends PathExpr {
        @Getter
        private final Unit unit;

        public UnitPathExpr(Unit unit) {
            this.unit = unit;
        }
    }
    public static final class PackagePathExpr extends PathExpr {
        @Getter
        private final Package aPackage;

        public PackagePathExpr(Package aPackage) {
            this.aPackage = aPackage;
        }
    }

    public static final class StructPathExpr extends PathExpr {
        @Getter
        private final Unit.Structure structure;

        public StructPathExpr(Unit.Structure structure) {
            this.structure = structure;
        }
    }
}
