package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.lexer.Region;
import lombok.Getter;

public final class DuplicateFailure extends Failure {

    @Getter
    private final String name;

    @Getter
    private final DuplicateType duplicateType;


    public DuplicateFailure(Region region, DuplicateType duplicateType, String name) {
        setRegion(region);
        this.name = name;
        this.duplicateType = duplicateType;
    }

    public enum DuplicateType {
        LOCAL_VARIABLE,
        STRUCT,
        CONST,
        ENUM,
    }
}
