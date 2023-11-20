package de.plixo.atic.macro;

import de.plixo.atic.lexer.Node;

public abstract class Macro {
    public abstract Node node(Node in);
}
