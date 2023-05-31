package de.plixo.atic.hir.item;

import com.google.gson.JsonElement;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

import java.util.List;

public abstract sealed class HIRItem permits HIRConst, HIRImport, HIRStruct {

    @Getter
    private final Region region;

    @Getter
    private final List<HIRAnnotation> annotations;


    public HIRItem(Region region, List<HIRAnnotation> annotations) {
        this.region = region;
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "HIRItem " + this.getClass().getName();
    }

    public abstract JsonElement toJson();

    public abstract String name();
}
