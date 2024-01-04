package de.plixo.galactic.lexer;

import de.plixo.galactic.lexer.tokens.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all tokens used by the lexer.
 */
public class GalacticTokens {

    private String[] keywords =
            new String[]{"class", "interface", "void", "int", "byte", "short", "long", "float",
                    "double", "boolean", "char", "fn", "if", "else", "new", "extends", "implements",
                    "return", "var", "import", "as", "is", "super", "this"};

    private String[] javaKeyworkds =
            new String[]{"abstract", "continue", "for", "new", "switch", "assert", "default",
                    "goto", "package", "synchronized", "boolean", "do", "if", "private", "this",
                    "break", "double", "implements", "protected", "throw", "byte", "else", "import",
                    "public", "throws", "case", "enum", "instanceof", "return", "transient",
                    "catch", "extends", "int", "short", "try", "char", "final", "interface",
                    "static", "void", "class", "finally", "long", "strictfp", "volatile", "const",
                    "float", "native", "super", "while",};


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
        tokens.add(new CharToken('|'));
        tokens.add(new CharToken('&'));
        tokens.add(new CharToken('%'));
        tokens.add(new CharToken('~'));
        tokens.add(new CharToken('^'));
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
        tokens.add(new CharToken('?'));
        for (String keyword : keywords) {
            tokens.add(new LiteralToken(keyword));
        }
        tokens.add(new WordToken());
        tokens.add(new NumberToken());
        tokens.add(new StringToken());
        return tokens;
    }

    public boolean isKeyword(String name) {
        return List.of(keywords).contains(name);
    }

    public boolean isJavaKeyword(String name) {
        return List.of(javaKeyworkds).contains(name);
    }

}
