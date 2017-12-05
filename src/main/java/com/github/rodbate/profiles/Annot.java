package com.github.rodbate.profiles;


public @interface Annot {
    String[] a() default {};

    int[] b() default {};

    boolean c() default true;
}
