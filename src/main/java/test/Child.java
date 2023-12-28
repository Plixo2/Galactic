package test;

public class Child {
    public static int TEST = 0;
    public String msg;

    public Child() {
        msg = "";
    }

    public Child(String msg) {
        this.msg = msg;
    }

    public void test() {
        msg = "Hello World";
    }

    public void t2(String[] args) {
        var c = get();
        if (c.msg.isEmpty()) {
            System.out.println("Hello World");
        }
    }
    public static void t2S(String[] args) {
        var c = get();
        if (c.msg.isEmpty()) {
            System.out.println("Hello World");
        }
    }

    public static Child get() {
        return new Child();
    }

}
