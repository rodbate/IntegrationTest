package com.github.rodbate.it.asm;

import org.junit.*;

/**
 *
 * Created by rodbate on 2018/1/29.
 */
public class CircleRefTest {

    @org.junit.Test
    public void testCircle() {
        new CA();
        System.out.println(System.getProperty("java.system.class.loader"));
    }

}

class CA {
    private CB cb = null;
}

class CB {
    private CA ca = new CA();
}
