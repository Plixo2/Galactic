package de.plixo.hir.typedef;

import com.google.gson.JsonElement;

public abstract sealed class HIRType permits HIRClassType, HIRFunctionType {

    public abstract JsonElement toJson();
}
