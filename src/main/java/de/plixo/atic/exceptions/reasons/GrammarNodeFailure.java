package de.plixo.atic.exceptions.reasons;

import de.plixo.atic.lexer.Node;

public final class GrammarNodeFailure extends Failure {
    public GrammarNodeFailure(String message, Node node) {
        setRegion(node.region());
        setNode(node);
        setMessage(message);
    }
}
