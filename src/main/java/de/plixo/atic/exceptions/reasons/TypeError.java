package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.types.AType;
import de.plixo.atic.lexer.Region;

public final class TypeError extends Failure {
    public TypeError(Region region, AType a, AType b) {
        setMessage("type test failed between " + a + " and " + b);
        setRegion(region);
    }
}
