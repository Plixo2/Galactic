package de.plixo.atic.tir.expr;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.plixo.atic.tir.tree.Package;
import de.plixo.atic.tir.tree.Unit;
import de.plixo.atic.typing.types.Primitive;
import de.plixo.atic.typing.types.Type;
import lombok.Getter;

public abstract sealed class PathExpr implements Expr {

    @Override
    public Type getType() {
        return Primitive.VOID;
    }

    public static final class UnitPathExpr extends PathExpr {
        @Getter
        private final Unit unit;

        public UnitPathExpr(Unit unit) {
            this.unit = unit;
        }

        @Override
        public JsonElement toJson() {
            var jsonObject = new JsonObject();
            jsonObject.addProperty("class", this.getClass().getSimpleName());
            jsonObject.addProperty("unit", unit.absolutName());
            return jsonObject;
        }
    }
    public static final class PackagePathExpr extends PathExpr {
        @Getter
        private final Package aPackage;

        public PackagePathExpr(Package aPackage) {
            this.aPackage = aPackage;
        }

        @Override
        public JsonElement toJson() {
            var jsonObject = new JsonObject();
            jsonObject.addProperty("class", this.getClass().getSimpleName());
            jsonObject.addProperty("package", aPackage.absolutName());
            return jsonObject;
        }
    }

    public static final class StructPathExpr extends PathExpr {
        @Getter
        private final Unit.Structure structure;

        public StructPathExpr(Unit.Structure structure) {
            this.structure = structure;
        }
        @Override
        public JsonElement toJson() {
            var jsonObject = new JsonObject();
            jsonObject.addProperty("class", this.getClass().getSimpleName());
            jsonObject.addProperty("struct", structure.absolutName());
            return jsonObject;
        }
    }



}
