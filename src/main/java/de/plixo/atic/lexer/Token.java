package de.plixo.atic.lexer;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;


public enum Token {
    COMMENT(".comment", "\\/\\/", "\\/\\/.*"),
    WHITESPACE("", "\\s", "\\s*"),
    USE("use", "use\\b", "use"),
    STRUCTURE("structure", "structure\\b", "structure"),
    INTERFACE("interface", "interface\\b", "interface"),
    FUNCTION("fn", "fn\\b", "fn"),
    IF("if", "if\\b", "if"),
    ELSE("else", "else\\b", "else"),
    ELIF("elif", "elif\\b", "elif"),
    RETURN("return", "return\\b", "return"),
    LET("let", "let\\b", "let"),
    NATIVE("native", "native\\b", "native"),
    ASSIGN_ARROW("->", "->", "(-|->)"),
    PARENTHESES_O("(", "\\(", "\\("),
    PARENTHESES_C(")", "\\)", "\\)"),
    BRACES_O("{", "\\{", "\\{"),
    BRACES_C("}", "\\}", "\\}"),
    BRACKET_O("[", "\\[", "\\["),
    BRACKET_C("]", "\\]", "\\]"),
    SEPARATOR(",", "\\,", "\\,"),
    DOT(".", "\\.", "\\."),
    COLON(":", ":", "(:)"),
    NON_EQUALS("!=", "!=", "(!=|!)"),
    EQUALS("==", "==", "(==|=)"),
    SMALLER_EQUALS("<=", "<=", "(<=|<)"),
    GREATER_EQUALS(">=", ">=", "(>=|>)"),
    OR("||", "\\|\\|", "(\\||\\|\\|)"),
    AND("&&", "\\&\\&", "(\\&|\\&\\&)"),
    NOT("!", "\\!", "\\!"),
    PLUS("+", "\\+", "(\\+)"),
    MINUS("-", "\\-", "(\\-)"),
    MUL("*", "\\*", "(\\*)"),
    DIV("/", "\\/", "(\\/)"),
    GREATER("<", "<", "(<)"),
    SMALLER(">", ">", "(>)"),
    HASH("#", "#", "(#)"),
    NUMBER("number", "[0-9]", "[0-9\\.]+"),
    KEYWORD("keyword", "\\w", "\\w+"),
    ASSIGN("=", "=", "="),
    END_OF_FILE("EOF", "$a^", "$a^"),

    ;
    public final Predicate<String> peek;
    public final Predicate<String> capture;
    public final String alias;

    Token(String alias, String peek, String capture) {
        this.alias = alias;



        if (Objects.equals(alias, "string")) {
            this.peek = Pattern.compile("^" + peek, Pattern.MULTILINE).asPredicate();
            this.capture = (string) -> {

                boolean waitForChar = true;
                var chars = string.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    char c = chars[i];
                    if (waitForChar) {
                        waitForChar = false;
                        continue;
                    }
                    if (c == '\\') {
                        waitForChar = true;
                    }
                    if (c == '"') {
                        if (i == chars.length - 2) {
                            return false;
                        }
                    }
                }
                return true;

            };
        } else {
            this.peek = Pattern.compile("^" + peek, Pattern.MULTILINE).asPredicate();
            if (capture.matches("\\w+")) {
                this.capture = (str) -> str.length() <= capture.length();
            } else {
                this.capture = Pattern.compile("^" + capture + "$", Pattern.MULTILINE).asPredicate();
            }
        }

    }
}
