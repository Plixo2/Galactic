package de.plixo.galactic.standalone;

import org.junit.jupiter.api.AfterAll;
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
        loadedClass = Container.loadStandalone("resources/tests/InlineTests.stella", "InlineTests.InlineTests");
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
}
