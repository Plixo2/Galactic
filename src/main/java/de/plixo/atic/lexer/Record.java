package de.plixo.atic.lexer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
public class Record {

    @Getter
    private final Token token;
    @Getter
    private final String data;

    @Getter
    private final Position position;

    @Override
    public String toString() {
        return "Record{" + "token=" + token + ", data='" + data + '\'' + ", position=" + position + '}';
    }
}
