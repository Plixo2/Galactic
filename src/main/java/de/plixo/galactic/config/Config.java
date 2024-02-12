package de.plixo.galactic.config;

import de.plixo.galactic.typed.StandardLibs;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.tomlj.Toml;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

    private final String source;
    private final String mainClass;
    private final String buildOutput;
    private final StandardLibs standardLibs;


    public static ConfigResult load(String tomlSource) {
        var result = Toml.parse(tomlSource);
        if (result.hasErrors()) {
            return new ConfigResult.Error(result.errors().stream().map(Object::toString).toList());
        }

        var source = result.get("source");
        if (source == null) {
            return new ConfigResult.Error(List.of("'source' key is missing"));
        }
        var main = result.get("main");
        if (main == null) {
            return new ConfigResult.Error(List.of("'main' key is missing"));
        }
        var build = result.get("build");
        if (build == null) {
            return new ConfigResult.Error(List.of("'build' key is missing"));
        }

        var imports = new ArrayList<StandardLibs.Lib>();
        var core = result.getTableOrEmpty("core");
        core.entrySet().forEach(entry -> {
            var path = entry.getKey();
            var name = entry.getValue().toString();
            imports.add(new StandardLibs.Lib(name, path));
        });
        var standardLibs = new StandardLibs("stella", imports);

        return new ConfigResult.Success(
                new Config(source.toString(), main.toString(), build.toString(), standardLibs));
    }


    public sealed interface ConfigResult {
        record Success(Config config) implements ConfigResult {
        }

        record Error(List<String> messages) implements ConfigResult {
        }
    }
}
