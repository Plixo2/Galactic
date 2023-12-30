package test;

import java.util.Objects;

public class Child {


    public void test(Object object) {
        var s = (String) object;
        System.out.println(s.length());
    }

}
