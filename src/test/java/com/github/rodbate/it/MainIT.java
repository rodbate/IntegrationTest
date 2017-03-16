package com.github.rodbate.it;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MainIT {

    private static Main main;


    @Before
    public void init(){
        main = new Main();
    }

    @After
    public void destroy(){
        main = null;
    }

    @Test
    public void testM1(){

        assertEquals(5, main.method1("abcde"));

    }
}
