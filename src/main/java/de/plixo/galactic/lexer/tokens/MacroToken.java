package de.plixo.galactic.lexer.tokens;

import org.w3c.dom.css.CSSStyleRule;

public final class MacroToken extends Token {
    public MacroToken() {
        super("macro");
    }

    @Override
    public boolean startsWith(char str) {
        return str == '#';
    }

    @Override
    public int matches(String str, int index) {
        var i = index + 1;
        for (; i < str.length(); i++) {
            var point = str.charAt(i);
            if (!isInAlphabet(point)) {
                if (i == index + 1)
                    return -1;
                return i;
            }
        }
        if (i == index + 1)
            return -1;
        return i;
    }

    private boolean isInAlphabet(char point) {
        return (point >= 'A' && point <= 'Z') || (point >= 'a' && point <= 'z');
    }

}
