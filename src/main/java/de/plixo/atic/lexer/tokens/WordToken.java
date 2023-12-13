package de.plixo.atic.lexer.tokens;

/**
 * Token for matching words (ascii letters a to z)
 */
public final class WordToken extends Token {
    public WordToken() {
        super("word");
    }

    @Override
    public boolean startsWith(char str) {
        return isInAlphabet(str);
    }

    @Override
    public int matches(String str, int index) {
        int i;
        for (i = index; i < str.length(); i++) {
            if (!isInWord(str.charAt(i))) {
                break;
            }
        }
        return i;
    }

    private boolean isInAlphabet(char point) {
        return (point >= 'A' && point <= 'Z') || (point >= 'a' && point <= 'z') || point == '_';
    }
    private boolean isInWord(char point) {
        return (point >= 'A' && point <= 'Z') || (point >= 'a' && point <= 'z') || point == '_'  || (point >= '0' && point <= '9');
    }

}
