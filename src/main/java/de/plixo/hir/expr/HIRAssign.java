package de.plixo.hir.expr;

import com.google.gson.JsonElement;

public final class HIRAssign extends HIRExpr {
    public HIRExpr object;
    public HIRExpr value;

    public HIRAssign(HIRExpr object, HIRExpr value) {
        this.object = object;
        this.value = value;
    }


    @Override
    public JsonElement toJson() {
        throw new NullPointerException("TODO");
    }
}
