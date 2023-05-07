package de.plixo.hir.item;

import com.google.gson.JsonElement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class HIRItem permits HIRConst, HIRImport, HIRStruct {

    @Getter
    private List<HIRAnnotation> annotations;

    public HIRItem(List<HIRAnnotation> annotations) {
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "HIRItem " + this.getClass().getName();
    }

    public abstract JsonElement toJson();

    public abstract String name();
}
