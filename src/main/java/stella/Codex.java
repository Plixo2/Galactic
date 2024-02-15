package stella;

public class Codex {

    //#region Add
    public static int add(int a, int b) {
        return a + b;
    }
    public static double add(double a, double b) {
        return a + b;
    }
    public static float add(float a, float b) {
        return a + b;
    }
    //#endregion

    //#region Subtract
    public static int subtract(int a, int b) {
        return a - b;
    }
    public static double subtract(double a, double b) {
        return a - b;
    }
    public static float subtract(float a, float b) {
        return a - b;
    }
    //#endregion

    //#region Multiply
    public static int multiply(int a, int b) {
        return a * b;
    }
    public static double multiply(double a, double b) {
        return a * b;
    }
    public static float multiply(float a, float b) {
        return a * b;
    }
    //#endregion

    //#region Divide
    public static int divide(int a, int b) {
        return a / b;
    }
    public static double divide(double a, double b) {
        return a / b;
    }
    public static float divide(float a, float b) {
        return a / b;
    }
    //#endregion

    //#region Mod
    public static int mod(int a, int b) {
        return a % b;
    }
    public static double mod(double a, double b) {
        return a % b;
    }
    public static float mod(float a, float b) {
        return a % b;
    }
    //#endregion

    //#region And
    public static boolean and(boolean a, boolean b) {
        return a && b;
    }
    //#endregion

    //#region Or
    public static boolean or(boolean a, boolean b) {
        return a || b;
    }
    //#endregion

    //#region Less
    public static boolean less(int a, int b) {
        return a < b;
    }
    public static boolean less(double a, double b) {
        return a < b;
    }
    public static boolean less(float a, float b) {
        return a < b;
    }
    //#endregion

    //#region LessEquals
    public static boolean lessEquals(int a, int b) {
        return a <= b;
    }
    public static boolean lessEquals(double a, double b) {
        return a <= b;
    }
    public static boolean lessEquals(float a, float b) {
        return a <= b;
    }
    //#endregion

    //#region Greater
    public static boolean greater(int a, int b) {
        return a > b;
    }
    public static boolean greater(double a, double b) {
        return a > b;
    }
    public static boolean greater(float a, float b) {
        return a > b;
    }
    //#endregion

    //#region GreaterEquals
    public static boolean greaterEquals(int a, int b) {
        return a >= b;
    }
    public static boolean greaterEquals(double a, double b) {
        return a >= b;
    }
    public static boolean greaterEquals(float a, float b) {
        return a >= b;
    }
    //#endregion

    //#region Equals
    public static boolean equals(int a, int b) {
        return a == b;
    }
    public static boolean equals(double a, double b) {
        return a == b;
    }
    public static boolean equals(float a, float b) {
        return a == b;
    }
    public static boolean equals(boolean a, boolean b) {
        return a == b;
    }
    //#endregion

    //#region NotEquals
    public static boolean notEquals(int a, int b) {
        return a != b;
    }
    public static boolean notEquals(double a, double b) {
        return a != b;
    }
    public static boolean notEquals(float a, float b) {
        return a != b;
    }
    public static boolean notEquals(boolean a, boolean b) {
        return a != b;
    }
    //#endregion

    //#region Negate
    public static int negate(int a) {
        return -a;
    }
    public static double negate(double a) {
        return -a;
    }
    public static float negate(float a) {
        return -a;
    }
    //#endregion

    public static double toDouble(int a) {
        return a;
    }
    public static int toInt(double a) {
        return (int) a;
    }

    public static String toString(int a) {
        return Integer.toString(a);
    }

    public static Object getNull() {
        return null;
    }

    public static void assertValue(boolean condition) {
        if (!condition && Codex.class.desiredAssertionStatus()) {
            throw new AssertionError("Assertion failed");
        }
    }

    public static String concat(String a, String b) {
        return a + b;
    }

    public static Object get(Object[] array, int index) {
        return array[index];
    }


}
