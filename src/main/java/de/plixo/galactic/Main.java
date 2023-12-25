package de.plixo.galactic;

import de.plixo.galactic.exception.FlairException;

import java.io.File;

public class Main {
    public static void main(String[] args) {
//        System.out.print("Hello World");
//        System.out.println(" file:///home/alexander/Downloads/\\aDownload");
        String mainClass = null;
        if (args.length >= 1) {
            mainClass = args[0];
        }
        var language = new Universe();
        try {
            var root = language.parse(new File("resources/project"));
            switch (root) {
                case Universe.Success success -> {
                    language.write(success.root(), mainClass);
                }
                case Universe.Error error -> {
                    System.err.println(error.exception().prettyPrint());
                }
            }
        } catch (FlairException e) {
            e.printStackTrace();
        }
        System.out.println("fin");
    }

}
