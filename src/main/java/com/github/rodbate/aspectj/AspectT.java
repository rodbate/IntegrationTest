package com.github.rodbate.aspectj;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;






@Component
@Aspect
public class AspectT {

    @Pointcut("execution(* com.github.rodbate..*.*(..))")
    public void pointCut() {

    }

    @Pointcut("@annotation(ds)")
    public void p2(AnnotationAs ds){

    }


    @Before("pointCut()")
    public void test(JoinPoint joinPoint) {
        System.out.println("before ----------------");
        System.out.println("method name" + joinPoint.getSignature().getName());
        System.out.println(Arrays.toString(joinPoint.getArgs()));
    }

    @Before("p2(ds)")
    public void test2(JoinPoint joinPoint, AnnotationAs ds) {

        Class declaringType = joinPoint.getSignature()
                                       .getDeclaringType();

        System.out.println(declaringType.getCanonicalName());
        System.out.println(ds.value());
    }
}

