package de.plixo.galactic.standalone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InlineTest {

    static Class<?> loadedClass;

    @BeforeAll
    public static void init() {
        loadedClass = Container.loadStandalone("resources/tests/InlineTests.stella",
                "InlineTests.InlineTests");
    }


    @Test
    public void testMath() {
        Container.run(loadedClass, "inlineMath");
    }

    @Test
    public void testObjects() {
        Container.run(loadedClass, "testObjects");
    }

    @Test
    public void testDistance() {
        var result = Container.run(loadedClass, "distance", 3, 4);
        assertEquals(5.0, result);
    }

    @Test
    public void sum() {
        var result = Container.run(loadedClass, "sum", 100);
        var sum = 0;
        for (int i = 0; i <= 100; i++) {
            sum += i;
        }
        assertEquals(sum, result);
    }

    @Test
    public void testIdentity() {
        var objects = new Object[]{"Hello", 0, null, new ArrayList<>(), new Object[0],};
        for (var input : objects) {
            var result = Container.run(loadedClass, "identity", input);
            assert result == input;
        }
    }

    @Test
    public void testFilter() {
        var objects = new Object[]{"Hello", 0, new ArrayList<>(), new Object[0],};
        var elements = new ArrayList<>(Arrays.asList(objects));
        for (var in : objects) {
            var result = Container.run(loadedClass, "filterNotEquals", elements, in);
            assert result instanceof List<?> lst && lst.size() == objects.length - 1;
        }
    }
}
