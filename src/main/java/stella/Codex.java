package stella;

import java.util.Objects;

public class Codex {
    public static int add(int a, int b) {
        return a + b;
    }
    public static double add(double a, double b) {
        return a + b;
    }

    public static String toString(int a) {
        return Integer.toString(a);
    }

    public static boolean equals(int a, int b) {
        return a == b;
    }
    public static boolean equals(double a, double b) {
        return a == b;
    }

    public static Object getNull() {
        return null;
    }

    public static void assertValue(boolean condition) {
        if (!condition && Codex.class.desiredAssertionStatus()) {
            throw new AssertionError("Assertion failed");
        }
    }
}
