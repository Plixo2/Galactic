package de.plixo.atic;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        var language = new Language();
        language.parse(new File("resources/project"));
    }

}
