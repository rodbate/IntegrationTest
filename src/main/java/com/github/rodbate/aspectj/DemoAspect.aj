package com.github.rodbate.aspectj;





public aspect DemoAspect {


    pointcut p1():
        execution(* com.github.rodbate..*.*(..));

    before(): p1() {
        thisJoinPoint.getSignature();
        System.out.println("before ... ");
    }



}
