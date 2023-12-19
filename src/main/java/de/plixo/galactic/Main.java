package de.plixo.galactic;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String name = null;
        if (args.length >= 1) {
            name = args[0];
        }
        var language = new Language(name);
        language.parse(new File("resources/project"));
    }

}
