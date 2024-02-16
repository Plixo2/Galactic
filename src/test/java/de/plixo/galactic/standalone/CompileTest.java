package de.plixo.galactic.standalone;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompileTest {

    static Class<?> loadedClass;

    @BeforeAll
    public static void init() {
        loadedClass = Container.loadStandalone("resources/tests/Nothing.stella",
                "Nothing.Nothing");
    }

    @Test
    public void testIdentity() {
        assert loadedClass != null;
    }

}
