package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.lexer.Region;
import lombok.Getter;

public final class ImportFailure extends Failure {
    @Getter
    private final ImportFailType failType;
    @Getter
    private final String name;

    public ImportFailure(Region region, ImportFailType failType, String name) {
        setRegion(region);
        this.failType = failType;
        this.name = name;
    }

    public enum ImportFailType {
        IMPORT_FIELD,
        IMPORT_PACKAGE,
        UNKNOWN_OBJECT,
        UNKNOWN_UNIT,
    }
}
