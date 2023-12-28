package de.plixo.galactic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String mainClass = null;
        if (args.length >= 1) {
            mainClass = args[0];
        }
        var language = new Universe();
        var root = language.parse(new File("resources/project"));
        switch (root) {
            case Universe.Success success -> {
                try (var out = new FileOutputStream("resources/out.jar")) {
                    language.write(out, success.root(), mainClass);
                }
            }
            case Universe.Error error -> {
                System.err.println(error.exception().prettyPrint());
                System.err.println("\n".repeat(2));
                error.exception().printStackTrace();
            }
        }
        System.out.println("fin");
    }

}
