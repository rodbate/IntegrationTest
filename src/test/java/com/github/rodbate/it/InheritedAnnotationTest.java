package com.github.rodbate.it;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;

/**
 *
 * Created by rodbate on 2018/3/8.
 */
public class InheritedAnnotationTest extends Assert {


    @Test
    public void test() {
        InheritedSampleAnnotation inheritedSampleAnnotation = CL.class.getAnnotation(InheritedSampleAnnotation.class);
        NotInheritedSampleAnnotation notInheritedSampleAnnotation = CL.class.getAnnotation(NotInheritedSampleAnnotation.class);
        assertTrue(inheritedSampleAnnotation != null);
        assertTrue(notInheritedSampleAnnotation == null);
    }



    @Documented
    @Inherited
    @Target({ ElementType.TYPE, ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @interface InheritedSampleAnnotation {}

    @InheritedSampleAnnotation
    @NotInheritedSampleAnnotation
    static class PA {}

    static class CL extends PA {}

    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface NotInheritedSampleAnnotation {}
}
