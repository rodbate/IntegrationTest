package com.github.rodbate.it.asm;


import org.junit.After;
import org.junit.Before;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * Created by rodbate on 2018/1/19.
 */
public class TestWeaver {

    private Path dstFile;
    private String dstClassName;
    private String dstClassInternalName;


    @Before
    public void setUp() {
        dstFile = Paths.get(".", "Testdst.class");
        dstClassName = Test.class.getName() + "_dst";
        dstClassInternalName = dstClassName.replaceAll("\\.", "/");
    }

    @After
    public void tearDown() throws IOException {
        //delete file
        if (dstFile != null) {
            Files.delete(dstFile);
        }
    }


    @org.junit.Test
    public void test() throws Throwable {
        testAsm();
        testLoadClass();
    }


    @org.junit.Test
    public void testAsm() throws IOException {
        ClassReader cr = new ClassReader(Test.class.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cr.accept(new ClassVisitorAdapter(Opcodes.ASM5, cw, dstClassInternalName), ClassReader.EXPAND_FRAMES);
        OutputStream os = Files.newOutputStream(dstFile, StandardOpenOption.CREATE);
        os.write(cw.toByteArray());
        os.close();
    }

    @org.junit.Test
    public void testLoadClass() throws Throwable {
        ClassFileClassLoader loader = new ClassFileClassLoader(dstFile.toString());
        Class<?> clazz = loader.loadClass(dstClassName, true);
        Constructor<?> cst = clazz.getConstructor();
        Object instance = cst.newInstance();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        //main method
        MethodHandle mainHandle = lookup.findStatic(clazz, "main", MethodType.methodType(void.class, String[].class));
        mainHandle.invokeExact(new String[0]);

        //printOne method
        lookup.findStatic(clazz, "printOne", MethodType.methodType(void.class)).invokeExact();
    }


    static class ClassFileClassLoader extends ClassLoader {
        private final Path filepath;

        //parent -> system classloader
        public ClassFileClassLoader(String filepath) {
            this.filepath = Paths.get(filepath);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            Class<?> clazz = null;
            InputStream in = null;
            try {
                in = Files.newInputStream(filepath, StandardOpenOption.READ);
                byte data[] = new byte[in.available()];
                int rn = in.read(data);
                if (rn > 0) {
                    clazz = defineClass(name, data, 0, data.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            if (clazz == null) {
                throw new ClassNotFoundException(name);
            }
            return clazz;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }
    }


    static class AtClassLoader extends URLClassLoader {

        public AtClassLoader(String filepath) throws MalformedURLException {
            super(new URL[] { new URL("file:" + filepath) });
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            final Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            try {
                Class<?> aClass = findClass(name);
                if (resolve) {
                    resolveClass(aClass);
                }
                return aClass;
            } catch (Exception e) {
                return super.loadClass(name, resolve);
            }
        }
    }


    static class ClassVisitorAdapter extends ClassVisitor implements Opcodes{

        private String dstClassName;
        public ClassVisitorAdapter(int api, ClassVisitor cv, String dstClassName) {
            super(api, cv);
            this.dstClassName = dstClassName;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, dstClassName, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return (mv != null /*&& !"<init>".equals(name)*/) ? new MethodVisitorAdapter(ASM5, mv) : null;
        }
    }

    static class MethodVisitorAdapter extends MethodVisitor implements Opcodes{

        public MethodVisitorAdapter(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            //enter method
            //System.out.println("Enter method!");
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Enter method: " + name);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            super.visitMethodInsn(opcode, owner, name, desc, itf);

            //exit method
            //System.out.println("Exit method!");
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Exit method: " + name);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }
}
