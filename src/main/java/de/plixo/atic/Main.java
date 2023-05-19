package de.plixo.atic;

import com.google.common.io.Resources;
import de.plixo.atic.lexer.Lexer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        var resource = Main.class.getResource("/cfg.txt");
        assert resource != null;
        String grammar = Resources.toString(resource, StandardCharsets.UTF_8);
        Language language = new Language();
        Lexer lexer = new Lexer(grammar);
        var config = new Language.ParseConfig("atic", lexer);
        language.readProject(new File("resources/project"), config);
    }
}
