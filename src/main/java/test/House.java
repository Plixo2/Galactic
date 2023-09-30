package test;

public class House extends TestObject {
    public static House INSTANCE = new House();

    public double number = 0;
    public void accept(TestObject object) {

    }

    public String getName() {
        return "the House";
    }

    public Car getCar() {
        return new Car();
    }
}
