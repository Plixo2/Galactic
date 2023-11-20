package de.plixo.atic.macro;

import de.plixo.atic.lexer.Node;

public class MacroTest extends Macro {
    @Override
    public Node node(Node in) {
        return in;
    }
}
