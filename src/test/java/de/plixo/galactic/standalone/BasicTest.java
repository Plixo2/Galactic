package de.plixo.galactic.standalone;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicTest {
    static Object[] objects = {"Hello", 0, null, new ArrayList<>(), new Object[0],};

    static Class<?> loadedClass;
    static int checks = 0;

    @BeforeAll
    public static void init() {
        loadedClass = Container.loadStandalone("resources/tests/BasicTest.stella", "BasicTest.BasicTest");
    }

    @Test
    public void testIdentity() {
        for (var input : objects) {
            var result = Container.run(loadedClass, "identity", input);
            checks += 1;
            assert result == input;
        }
    }

    @Test
    public void testFilter() {
        var objects = new Object[]{"Hello", 0, new ArrayList<>(), new Object[0],};
        var elements = new ArrayList<>(Arrays.asList(objects));
        for (var in : objects) {
            var result = Container.run(loadedClass, "filterNotEquals", elements, in);
            checks += 1;
            assert result instanceof List<?> lst && lst.size() == objects.length - 1;
        }
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println(STR."\{checks} checks passed");
    }
}
