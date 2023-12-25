package test;

public interface TestInterface {

    String asString();

    default String asString2() {
        return "";
    }
}
