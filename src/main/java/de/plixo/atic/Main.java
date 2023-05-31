package de.plixo.atic;

import com.google.common.io.Resources;
import de.plixo.atic.lexer.Lexer;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        var resource = Main.class.getResource("/cfg.txt");
        String grammar;
        try {
            grammar = Resources.toString(Objects.requireNonNull(resource), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("cant load grammar resource", e);
        }
        var lexer = new Lexer(grammar,"unit");
        var language = new Language(new ParseConfig("atic", lexer, false));
        language.parse(new File("resources/project"));
    }
}
