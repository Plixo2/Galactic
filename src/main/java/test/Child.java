package test;

public class Child {

    public void test() {
        System.out.println("test");
        var s = "test";

        if (s.contains("t")) {
            System.out.println("contains t");
        } else {
            System.out.println("does not contain t");
        }
    }


}
