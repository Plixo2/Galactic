
import java.util.Arrays;
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
        String hello = System.getProperty("hello");
        System.setProperty("hello", "bye");
        boolean hello2 = true;
        hello = System.getProperty("hello");
        other(args);
        Object first = getArg(0, args);
        System.out.println(first.equals("some"));
        System.out.println(hello.contains("hello"));
        System.out.println(hello.contains("bye"));
        System.out.println(hello);
    }

    public static boolean other(String[] args) {
        System.out.println(Arrays.toString(args));
        return false;
    }

    public static Object getArg(int index, String[] args) {
        Arrays.asList(args).get(index);
        return "Hello";
    }
}