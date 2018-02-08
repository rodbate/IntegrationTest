package com.github.rodbate.it;


import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestCore {


    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private String ffff;



    @Test
    public void arrays() throws Throwable {
        Class<?> arrayRef = Integer[][][].class;

        while (arrayRef.isArray()) {
            System.out.println("Array Ref: " + arrayRef.getName());
            arrayRef = arrayRef.getComponentType();
        }
        System.out.println("Element Ref: " + arrayRef.getName());

        MethodHandle methodHandle = MethodHandles.lookup().findVirtual(String.class, "trim", MethodType.methodType(String.class));
        Object rs = methodHandle.invoke("  aaaa  ");
        System.out.println(rs);

        MethodHandle mh = MethodHandles.lookup().findSpecial(TestCore.class, "toString", MethodType.methodType(String.class), TestCore.class);
        TestCore testCore = new TestCore();
        mh = mh.bindTo(testCore);
        rs = mh.invoke();
        System.out.println(rs);


        //MethodHandles.constant()
        MethodHandle ffffg = lookup.findGetter(TestCore.class, "ffff", String.class);
        MethodHandle ffffs = lookup.findSetter(TestCore.class, "ffff", String.class);
        ffffs.invoke(testCore, "mmmm");
        rs = ffffg.invoke(testCore);
        System.out.println(rs);

        //access mode: strict
        Method method = TestCore.class.getDeclaredMethod("arrays");
        System.out.println("Method arrays() strict mode: " + Modifier.isStrict(method.getModifiers()));
    }















    public static void main(String[] args) {


        /*JUnitCore core = new JUnitCore();


        core.addListener(new RunListener(){
            @Override
            public void testRunStarted(Description description) throws Exception {
                System.out.println("testRunStarted: " + description.getMethodName());
            }

            @Override
            public void testStarted(Description description) throws Exception {
                System.out.println("testStarted: " + description.getMethodName());
            }
        });

        core.run(SuiteTest.class);*/

        new JUnitCore().run(LuceneTest.class);

    }
}
