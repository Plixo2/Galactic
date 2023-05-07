package de.plixo.atic;

import de.plixo.atic.lexer.Lexer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static String GRAMMAR_FILE = "resources/cfg.txt";

    public static void main(String[] args) {
            String grammar;
            try {
                grammar =
                        FileUtils.readFileToString(new File(GRAMMAR_FILE), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Language language = new Language();
            Lexer lexer = new Lexer(grammar);
            var config = new Language.ParseConfig("atic", lexer);
            language.readProject(new File("resources/project"), config);
    }
}
