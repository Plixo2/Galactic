package de.plixo.atic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String name = null;
        if (args.length >= 1) {
            name = args[0];
        }
        var language = new Language(name);
        language.parse(new File("resources/project"));


        StringBuilder params = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            params.append('"').append(args[i]).append('"').append(" ");
        }

        var emulatorLocation = new File("resources");
        var absolutePath = emulatorLocation.getAbsolutePath();
        var cmd = "java -jar out.jar " + params;
        var fullCmd = "cd \"" + absolutePath + "\" && " + cmd;
        var builder = new ProcessBuilder("cmd.exe", "/c", fullCmd);
        builder.redirectError(new File("resources/err.txt"));
        try {
            final Process process = builder.start();
            try {
                var r = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while (process.isAlive()) {
                    String line = r.readLine();
                    if (line != null) {
                        System.out.println(line);
                    }
                }
                final int exitStatus = process.waitFor();
                if (exitStatus == 0) {
                    System.out.println("External Jar Started Successfully.");
                    System.exit(0);
                } else {
                    System.out.println(
                            "There was an error starting external Jar. Perhaps path issues. Use exit code " +
                                    exitStatus + " for details.");
                    System.exit(1);
                }
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException: " + ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println("IOException. Faild to start process. Reason: " + ex.getMessage());
        }
    }

}
