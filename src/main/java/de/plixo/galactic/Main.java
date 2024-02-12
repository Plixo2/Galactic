package de.plixo.galactic;

import de.plixo.galactic.config.CLIParser;
import de.plixo.galactic.config.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        var cliParser = new CLIParser(Arrays.stream(args).toList());
        if (cliParser.contains("--help") || cliParser.contains("-h")) {
            printHelp();
            return;
        }
        var configFile = getConfigFile(cliParser);
        var config = parseConfig(configFile);

        var language = new Universe();
        var startTimeCompile = System.currentTimeMillis();
        var root = language.parse(new File(config.source()), config.standardLibs());
        var parseTime = System.currentTimeMillis() - startTimeCompile;
        System.out.println(STR."Parsing Took \{parseTime} ms");

        switch (root) {
            case Universe.Success success -> {
                var startTimeWrite = System.currentTimeMillis();
                var debug = cliParser.contains("--debug") || cliParser.contains("-d");
                try (var out = new FileOutputStream(config.buildOutput())) {
                    language.write(out, success.root(), config.mainClass(), debug);
                }
                var writeTime = System.currentTimeMillis() - startTimeWrite;
                System.out.println(STR."Writing to \{config.buildOutput()} took \{writeTime} ms");
            }
            case Universe.Error error -> {
                System.err.println(error.exception().prettyPrint());
                System.err.println("\n");
                error.exception().printStackTrace(System.err);
            }
        }
    }

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println(" --config <path to config file>, -c <path to config file>");
        System.out.println("   Specifies the path to the config file");
        System.out.println(" --debug, -d");
        System.out.println("   Dumps debug information");
        System.out.println(" --help, -h");
        System.out.println("   Prints this help message");
    }

    private static File getConfigFile(CLIParser cliParser) {
        var argLong = cliParser.getArg("--config");
        if (argLong != null) {
            return new File(argLong);
        }
        var argShort = cliParser.getArg("-c");
        if (argShort != null) {
            return new File(argShort);
        }
        var direct = new File("config.toml");
        if (direct.exists()) {
            return direct;
        }
        var resource = new File("resources/config.toml");
        if (resource.exists()) {
            return resource;
        }
        throw new RuntimeException(
                "No config file found, please provide one with --config or place it in the root of the project as config.toml");
    }


    private static Config parseConfig(File file) {
        String src;
        try {
            src = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var configResult = Config.load(src);
        switch (configResult) {
            case Config.ConfigResult.Error error -> {
                throw new RuntimeException(STR."Config is invalid: \n\{error.messages()}");
            }
            case Config.ConfigResult.Success success -> {
                return success.config();
            }
        }
    }

}
