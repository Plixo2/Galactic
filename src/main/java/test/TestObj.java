package test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TestObj implements I2 {

    public TestObj(TestObj testObj, double integer) {
        this.testObj = testObj;
        this.integer = (int) integer;
    }

    public TestObj(TestObj testObj) {
        this.testObj = testObj;
    }

    public TestObj(TestObj testObj, int integer, int[] intArray) {
        this.testObj = testObj;
        this.integer = integer;
        this.intArray = intArray;
    }

    public TestObj() {
    }

    public TestObj testObj;
    public int integer;
    public int[] intArray;
    public Object obj;
    public Object[] objArray;
    public List<String> strings;


    public void test() {
        var value = "hello world";
        Consumer<String> func = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(value);
            }
        };
    }

    @Override
    public String asString() {
        return toString();
    }

    @Override
    public String toString() {
        return "TestObj{" + "testObj=" + testObj + ", integer=" + integer + ", intArray=" +
                Arrays.toString(intArray) + ", obj=" + obj + ", objArray=" +
                Arrays.toString(objArray) + ", strings=" + strings + '}';
    }
}
