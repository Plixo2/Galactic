package de.plixo.galactic.standalone;

import de.plixo.galactic.Universe;
import de.plixo.galactic.codegen.JarOutput;
import de.plixo.galactic.typed.StandardLibs;
import de.plixo.galactic.typed.path.Package;
import lombok.SneakyThrows;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class Container {
    //    private static final String TEST_BUILD = "resources/tests/build.jar";
    private final Universe universe;
    private final StandardLibs standardLibs = new StandardLibs("stella",
            List.of(new StandardLibs.Lib("resources/library/Core.stella", "Core"),
                    new StandardLibs.Lib("resources/library/Math.stella", "Math")));

    public Container() {
        universe = new Universe();
    }

    public Universe.CompileResult load(String path) {
        return universe.parse(new File(path), standardLibs);
    }

    public @Nullable Universe.Success assertSuccess(Universe.CompileResult result) {
        return switch (result) {
            case Universe.Success success -> success;
            case Universe.Error error -> {
                System.err.println(error.exception().prettyPrint());
                System.err.println("\n");
                error.exception().printStackTrace(System.err);
                yield null;
            }
        };
    }

    @SneakyThrows
    public Class<?> build(Universe.Success success, String main) {
        if (success.root() instanceof Package _package) {
            var stringMap = new HashMap<String, byte[]>();
            for (var unit : _package.getUnits()) {
                var result = universe.compileUnit(unit, success.root());
                for (JarOutput jarOutput : result) {
                    var name = jarOutput.path();
                    name = name.replace("/", ".").replace(".class", "");
                    stringMap.put(name, jarOutput.data());
                }
            }
            var classLoader =
                    new ByteArrayClassLoader(URLClassLoader.getSystemClassLoader(), stringMap);
            return classLoader.loadClass(main);
//        }
//        else if (success.root() instanceof Unit unit) {
//            var bytes = universe.compileUnit(unit, success.root());
//            var stringMap = new HashMap<String, byte[]>();
//            var name = bytes.path();
//            name = name.split("\\.")[0];
//            stringMap.put(name, bytes.data());
//            ByteArrayClassLoader classLoader =
//                    new ByteArrayClassLoader(URLClassLoader.getSystemClassLoader(), stringMap);
//            return classLoader.loadClass(name);
        } else {
            throw new IllegalStateException("Root is not a Package");
        }
    }

    @SneakyThrows
    public static Object run(Class<?> loadedClass, String name, Object... objects) {
        var main = getMethod(loadedClass, name);
        return main.invoke(null, objects);
    }

    private static Method getMethod(Class<?> loadedClass, String name) {
        return Arrays.stream(loadedClass.getMethods())
                .filter(method -> method.getName().equals(name)).findFirst().orElseThrow();
    }

    public static Class<?> loadStandalone(String file, String main) {
        var container = new Container();
        var compilation = container.load(file);
        var success =
                Objects.requireNonNull(container.assertSuccess(compilation), "Compile failed");
        return container.build(success, main);
    }
}
