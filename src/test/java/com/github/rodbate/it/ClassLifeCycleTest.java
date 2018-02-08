package com.github.rodbate.it;

import org.junit.Test;

/**
 *
 * Created by rodbate on 2018/1/30.
 */
public class ClassLifeCycleTest {

    @Test
    public void testD() {
         new B();
    }

}


class A {
    static int a = 100;

    static {
        System.out.println("A::B.b = " + B.b);
        System.out.println("A::B.AAA = " + B.AAA);
        System.out.println("C.c = " + C.c);
    }
}

class C {
    static final int c = 11111;
    static {
        System.out.println("C CLASS INITIALIZER");
    }
}

class B extends A {
    static int b = 10;
    static final int AAA;
    static {
        System.out.println("B::B.b = " + B.b);
        AAA = 10000;
    }
}