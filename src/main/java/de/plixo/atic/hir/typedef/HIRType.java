package de.plixo.atic.hir.typedef;

import com.google.gson.JsonElement;
import de.plixo.atic.lexer.Region;
import lombok.Getter;

public abstract sealed class HIRType permits HIRClassType, HIRFunctionType {

    @Getter
    private final Region region;

    public HIRType(Region region) {
        this.region = region;
    }

    public abstract JsonElement toJson();
}
