package de.plixo.atic;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;


public enum Token {
    COMMENT(".comment", "\\/\\/", "\\/\\/.*"),
    WHITESPACE("", "\\s", "\\s*"),
    USE("use", "use\\b", "use"),
    CLASS("class", "class\\b", "class"),
    INTERFACE("interface", "interface\\b", "interface"),
    VOID("void","void\\b","void"),
    INT("int","int\\b","int"),
    BYTE("byte","byte\\b","byte"),
    SHORT("short","short\\b","short"),
    LONG("long","long\\b","long"),
    FLOAT("float","float\\b","float"),
    DOUBLE("double","double\\b","double"),
    BOOLEAN("boolean","boolean\\b","boolean"),
    CHAR("char","char\\b","char"),
    FUNCTION("fn", "fn\\b", "fn"),
    IF("if", "if\\b", "if"),
    NEW("new", "new\\b", "new"),
    ELSE("else", "else\\b", "else"),
    EXTENDS("extends", "extends\\b", "extends"),
    IMPLEMENTS("implements", "implements\\b", "implements"),
    RETURN("return", "return\\b", "return"),
    VAR("var", "var\\b", "var"),
//    NATIVE("native", "native\\b", "native"),
    ASSIGN_ARROW("->", "->", "(-|->)"),
    SINGLE_EXPR_ARROW("=>", "=>", "(=|=>)"),
    PARENTHESES_O("(", "\\(", "\\("),
    PARENTHESES_C(")", "\\)", "\\)"),
    BRACES_O("{", "\\{", "\\{"),
    BRACES_C("}", "\\}", "\\}"),
    BRACKET_O("[", "\\[", "\\["),
    BRACKET_C("]", "\\]", "\\]"),
    SEPARATOR(",", "\\,", "\\,"),
    DOT(".", "\\.", "\\."),
    COLON(":", ":", "(:)"),
    SEMICOLON(";", ";", "(;)"),
    NON_EQUALS("!=", "!=", "(!=|!)"),
    EQUALS("==", "==", "(==|=)"),
    AT("@", "@", "(@)"),
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
    STRING("string", "\"", "text"),
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
