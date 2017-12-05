package com.github.rodbate.aspectj;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TestM {

    public void method(int a){
        System.out.println("method ----------");
    }

    @AnnotationAs("vate")
    public void test2() {

    }


    public static void main(String[] args) {

       AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.github.rodbate.aspectj");

        TestM bean = context.getBean(TestM.class);

        bean.method(10);


    }
}
