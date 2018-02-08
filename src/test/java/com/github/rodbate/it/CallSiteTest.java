package com.github.rodbate.it;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.lang.invoke.*;

/**
 *
 * Created by rodbate on 2018/1/18.
 */
public class CallSiteTest {


    private static void printArgs(Object... args) {
        System.out.println(java.util.Arrays.deepToString(args));
    }

    private static final MethodHandle printArgs;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class thisClass = lookup.lookupClass();
        try {
            printArgs = lookup.findStatic(thisClass, "printArgs", MethodType.methodType(void.class, Object[].class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static CallSite bootstrapDynamic(MethodHandles.Lookup caller, String name, MethodType type) {
        // ignore caller and name, but match the type:
        return new ConstantCallSite(printArgs.asType(type));
    }


    final int supplementaryCodePoint = 0x10000;
    final int minHighSurrogate = 0xD800;
    final int minLowSurrogate = 0xDC00;

    @Test
    public void testUTF16SurrogatePair() {
        int codePoint = 0x10437;    //character: 𐐷
        char str[] = new char[2];

        int codePointR = codePoint - supplementaryCodePoint;
        //high surrogate
        str[0] = (char) ((codePointR >> 10) + minHighSurrogate);
        //low surrogate
        str[1] = (char) ((codePoint & 0x3FF) + minLowSurrogate);
        Assert.assertTrue("\uD801\uDC37".equals(new String(str)));
    }





    @Test
    public void test() {
        int floatPositiveInfinity = 0x7f800000;
        Assert.assertTrue(floatPositiveInfinity == Float.floatToRawIntBits(Float.POSITIVE_INFINITY));

        byte bb = (byte) 0x8f;
        Assert.assertTrue((short)0xff8f == (short)bb);

        int abs = 0x80000011;
        System.out.println(abs + " : " + Integer.MIN_VALUE);
        System.out.println(Math.abs(abs));
        System.out.println(abs + 0x80000000L);
        //2^32
        //Assert.assertTrue(0x7fffffff == Math.abs(abs));
        Assert.assertTrue(0x100000000L + abs - 2  == Math.abs(abs));

    }



    @Test
    public void testClassFileFormat() throws IOException {
        ClassReader cr = new ClassReader(SuiteTest.class.getName());
        System.out.println(cr.getClassName());
    }
}
