package de.plixo.galactic.config;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public class CLIParser {

    private final List<String> args;

    public @Nullable String getArg(String arg) {
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).equals(arg) && (i + 1) < args.size()) {
                return args.get(i + 1);
            }
        }
        return null;
    }

    public boolean contains(String arg) {
        return args.contains(arg);
    }

}
