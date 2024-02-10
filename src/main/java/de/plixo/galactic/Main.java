package de.plixo.galactic;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    private final static String PATH = "resources/standalone/HelloWorld.stella";
    private final static @Nullable String MAIN_CLASS = "HelloWorld/HelloWorld";
    private final static String BUILD_PATH = "resources/build.jar";

    public static void main(String[] args) throws IOException {

//        var PATH = "resources/tests/InlineTests.stella";
//        String MAIN_CLASS = "InlineTests/InlineTests";

        var startTimeCompile = System.currentTimeMillis();

        var language = new Universe();
        var root = language.parse(new File(PATH));

        var parseTime = System.currentTimeMillis() - startTimeCompile;
        System.out.println(STR."Parsing Took \{parseTime} ms");

        switch (root) {
            case Universe.Success success -> {
                var startTimeWrite = System.currentTimeMillis();
                try (var out = new FileOutputStream(BUILD_PATH)) {
                    language.write(out, success.root(), MAIN_CLASS);
                }
                var writeTime = System.currentTimeMillis() - startTimeWrite;
                System.out.println(STR."Writing to \{BUILD_PATH} took \{writeTime} ms");
            }
            case Universe.Error error -> {
                System.err.println(error.exception().prettyPrint());
                System.err.println("\n");
                error.exception().printStackTrace(System.err);
            }
        }
    }

}
