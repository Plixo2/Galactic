package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.lexer.Region;
import de.plixo.atic.typing.types.Type;

public final class TypeError extends Failure{
    public TypeError(Region region, Type a, Type b) {
        setMessage("type test failed between " + a + " and " + b);
        setRegion(region);
    }
}
