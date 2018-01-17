package com.github.rodbate.fts;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class InvokeMethod {

    final static MethodHandles.Lookup PUBLIC_LOOK_UP = MethodHandles.publicLookup();


    public String to(Number num) {
        return num.toString();
    }

    public static String toS(Number num) {
        return num.toString();
    }
    public static MethodHandles.Lookup lookup(){
        return MethodHandles.lookup();
    }


    public static void main(String[] args) throws Throwable {

        //MethodHandle startsWith = lookup().findVirtual(InvokeMethod.class, "to", MethodType.methodType(String.class, Number.class));
        /*MethodHandle startsWith = PUBLIC_LOOK_UP.findVirtual(String.class, "startsWith", MethodType.methodType(boolean.class, String.class));

        Object str = "12345";
        boolean rs = (Boolean) startsWith.invoke("12345", "1");
        System.out.println(rs);*/

        MethodHandle toS = PUBLIC_LOOK_UP.findStatic(InvokeMethod.class, "toS", MethodType.methodType(String.class, Number.class));
        MethodHandle to = PUBLIC_LOOK_UP.findVirtual(InvokeMethod.class, "to", MethodType.methodType(String.class, Number.class));
        //Object rs = toS.invoke(11111);
        Object rs2 = to.bindTo(new InvokeMethod()).invoke(11111);
        System.out.println(rs2);
        System.out.println(Long.MAX_VALUE);
    }
}
