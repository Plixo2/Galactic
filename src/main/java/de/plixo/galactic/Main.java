package de.plixo.galactic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class Main {
    public static void main(String[] args) throws IOException {
        var runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        var arguments = runtimeMxBean.getInputArguments();
        if (!arguments.contains("--enable-preview")) {
            throw new RuntimeException(
                    "Please enable preview features with the '--enable-preview' vm argument");
        }


        String mainClass = "project/Main";
        var language = new Universe();
        var root = language.parse(new File("resources/project"));
        switch (root) {
            case Universe.Success success -> {
                try (var out = new FileOutputStream("resources/build.jar")) {
                    language.write(out, success.root(), mainClass);
                }
            }
            case Universe.Error error -> {
                System.err.println(error.exception().prettyPrint());
                System.err.println("\n");
                error.exception().printStackTrace(System.err);
            }
        }
        System.out.println("fin");
    }

}
