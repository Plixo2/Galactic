package de.plixo.galactic.lexer;

import de.plixo.galactic.lexer.tokens.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all tokens used by the lexer.
 */
public class GalacticTokens {
    public List<Token> tokens() {
        var tokens = new ArrayList<Token>();
        tokens.add(new WhiteSpaceToken());
        tokens.add(new CommentToken());
        tokens.add(new LiteralToken("->"));
        tokens.add(new LiteralToken("=>"));
        tokens.add(new LiteralToken("!="));
        tokens.add(new LiteralToken("=="));
        tokens.add(new LiteralToken("<="));
        tokens.add(new LiteralToken(">="));
        tokens.add(new LiteralToken("||"));
        tokens.add(new LiteralToken("&&"));
        tokens.add(new CharToken('{'));
        tokens.add(new CharToken('}'));
        tokens.add(new CharToken('['));
        tokens.add(new CharToken(']'));
        tokens.add(new CharToken('('));
        tokens.add(new CharToken(')'));
        tokens.add(new CharToken('.'));
        tokens.add(new CharToken(';'));
        tokens.add(new CharToken(','));
        tokens.add(new CharToken(':'));
        tokens.add(new CharToken('='));
        tokens.add(new CharToken('+'));
        tokens.add(new CharToken('-'));
        tokens.add(new CharToken('/'));
        tokens.add(new CharToken('*'));
        tokens.add(new CharToken('!'));
        tokens.add(new CharToken('<'));
        tokens.add(new CharToken('>'));
        tokens.add(new CharToken('@'));
        tokens.add(new CharToken('#'));
        tokens.add(new LiteralToken("class"));
        tokens.add(new LiteralToken("interface"));
        tokens.add(new LiteralToken("void"));
        tokens.add(new LiteralToken("int"));
        tokens.add(new LiteralToken("byte"));
        tokens.add(new LiteralToken("short"));
        tokens.add(new LiteralToken("long"));
        tokens.add(new LiteralToken("float"));
        tokens.add(new LiteralToken("double"));
        tokens.add(new LiteralToken("boolean"));
        tokens.add(new LiteralToken("char"));
        tokens.add(new LiteralToken("fn"));
        tokens.add(new LiteralToken("if"));
        tokens.add(new LiteralToken("else"));
        tokens.add(new LiteralToken("new"));
        tokens.add(new LiteralToken("extends"));
        tokens.add(new LiteralToken("implements"));
        tokens.add(new LiteralToken("return"));
        tokens.add(new LiteralToken("var"));
        tokens.add(new LiteralToken("import"));
        tokens.add(new LiteralToken("as"));
        tokens.add(new WordToken());
        tokens.add(new NumberToken());
        tokens.add(new StringToken());
        return tokens;
    }
}
