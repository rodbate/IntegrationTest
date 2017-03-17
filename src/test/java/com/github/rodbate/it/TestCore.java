package com.github.rodbate.it;


import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;

public class TestCore {


    public static void main(String[] args) {


        JUnitCore core = new JUnitCore();


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

        core.run(SuiteTest.class);

    }
}
