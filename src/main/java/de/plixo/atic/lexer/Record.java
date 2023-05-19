package de.plixo.atic.lexer;

import de.plixo.atic.Token;

public record Record(Token token, String data, Position position) {

    @Override
    public String toString() {
        return "Record{" + "token=" + token + ", data='" + data + '\'' + ", position=" + position +
                '}';
    }
}
