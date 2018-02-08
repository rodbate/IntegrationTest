package com.github.rodbate.it.asm;

import org.junit.*;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * jvm instruction set test
 *
 * Created by rodbate on 2018/1/25.
 */
public class InstructionSetTest implements Opcodes {




    @org.junit.Test
    public void testCompIns() {

        //generate code jvm8
        /*public class CompExam {
            public CompExam(){}
            public static void loop() {
                for (int i = 0; i < 100; i++) {
                    //loop
                }
            }
        }*/


        ClassWriter cw = new ClassWriter(0);

        //class
        cw.visit(V1_8, ACC_PUBLIC, "com/github/rodbate/it/asm/CompExam", null, "java/lang/Object", null);
        //source file
        cw.visitSource("CompExam.java", null);

        {
            //Field
            FieldVisitor fv = cw.visitField(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "INI", "Ljava/lang/String;", null, "null");
            fv.visitAnnotation("Ljava/lang/Deprecated;", true);

            //private final Object lock = new Object();
            cw.visitField(ACC_PRIVATE | ACC_FINAL, "lock", "Ljava/lang/Object;", null, null);
        }

        {
            //method <init>
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();

            //super()
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

            //initialize field lock
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitFieldInsn(PUTFIELD, "com/github/rodbate/it/asm/CompExam", "lock", "Ljava/lang/Object;");


            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("<init>");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            mv.visitInsn(RETURN);
            mv.visitEnd();
        }

        {
            //method loop
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "loop", "()V", null, null);
            mv.visitCode();
            //iconst_0
            mv.visitInsn(ICONST_0);
            //istore_1  #int i = 0
            mv.visitVarInsn(ISTORE, 0);
            //label
            Label label1 = new Label();
            mv.visitLabel(label1);

            //iload_0  push the local variable of index 0 to the operand stack
            mv.visitVarInsn(ILOAD, 0);
            //bipush 100
            mv.visitIntInsn(BIPUSH, 100);

            Label label2 = new Label();
            //if_icompge
            mv.visitJumpInsn(IF_ICMPGE, label2);
            //++i
            mv.visitIincInsn(0, 1);

            mv.visitJumpInsn(GOTO, label1);

            //label2 #end loop
            mv.visitLabel(label2);
            mv.visitInsn(RETURN);
            mv.visitLocalVariable("i", "I", null, label1, label2, 0);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }

        {
            //method add0
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "add0", "(II)I", null, null);
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitCode();
            mv.visitLabel(l1);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(IADD);
            mv.visitLabel(l2);
            mv.visitInsn(IRETURN);
            mv.visitLocalVariable("a", "I", null, l1, l2, 1);
            mv.visitLocalVariable("b", "I", null, l1, l2, 2);
            mv.visitEnd();
        }

        {
            //method add
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "add", "(II)I", null, null);
            //push this
            mv.visitVarInsn(ALOAD, 0);
            //push local variable at index 1
            mv.visitVarInsn(ILOAD, 1);
            //push local variable at index 2
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/rodbate/it/asm/CompExam", "add0", "(II)I", false);
            //store computation result to local variable 1
            mv.visitVarInsn(ISTORE, 1);

            //super hashCode
            //mv.visitVarInsn(ALOAD, 0);
            //mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);

            //store toString() result
            /*mv.visitVarInsn(ASTORE, 3);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);*/

            //do not use variable 3 to store toString() result
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

            //" s" + " s"
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);

            //append
            mv.visitLdcInsn("super toString()");
            //invoke new <init> method
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);

            //append
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            //toString()
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);


            //newarray
            //push array length
            mv.visitIntInsn(BIPUSH, 10);
            //new int[10]
            mv.visitIntInsn(NEWARRAY, T_INT);
            //int array[] = new int[10]
            mv.visitVarInsn(ASTORE, 3);

            //array[0] = 100 iastore   arrayref,index,value
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(BIPUSH, 0);
            mv.visitIntInsn(BIPUSH, 100);
            //mv.visitLdcInsn(0);
            //mv.visitLdcInsn(1000);
            mv.visitInsn(IASTORE);

            //get array[0]
            mv.visitIntInsn(ALOAD, 3);
            //push index
            mv.visitIntInsn(BIPUSH, 0);
            mv.visitInsn(IALOAD);
            //store value
            mv.visitIntInsn(ISTORE, 4);


            mv.visitIntInsn(BIPUSH, 5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Thread");
            mv.visitIntInsn(ASTORE, 5);

            //multi dimension array
            mv.visitIntInsn(BIPUSH, 8);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ICONST_2);
            mv.visitMultiANewArrayInsn("[[[[Ljava/lang/Thread;", 4);
            mv.visitIntInsn(ASTORE, 6);

            mv.visitVarInsn(ILOAD, 1);
            //return computation  result at top of operand stack
            mv.visitInsn(IRETURN);
        }

        {
            //throw exception
            /*
             *      public void ex(int i) throws IOException {
             *          if (i == 0) {
             *              throw new IOException();
             *          }
             *      }
             */
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "ex", "(I)V", null, new String[]{"java/io/IOException"});

            Label l1 = new Label();
            //push variable i to stack
            mv.visitIntInsn(ILOAD, 1);
            //if i == 0 jump
            mv.visitJumpInsn(IFNE, l1);
            //throw ex
            mv.visitTypeInsn(NEW, "java/io/IOException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/io/IOException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);

            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
        }

        {
            /*
             * use JSR instruction to implement finally block
             *
             * public void tryCatchFinallyBlock() {
                    try {
                        add(1, 2);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        System.out.println("finally ... ");
                    }
                }
             */

            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "tryCatchFinallyBlock", "()V", null, null);

            Label l1 = new Label();
            Label l2 = new Label();
            Label l3 = new Label();
            //finally start label
            Label l4 = new Label();
            Label l5 = new Label();
            //return from this method
            Label l6 = new Label();

            mv.visitTryCatchBlock(l1, l2, l3, "java/lang/Exception");
            mv.visitTryCatchBlock(l1, l5, l5, null);
            //try{
            mv.visitLabel(l1);
            //load this reference
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ICONST_2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/rodbate/it/asm/CompExam", "add", "(II)I", false);
            mv.visitInsn(POP);
            mv.visitJumpInsn(JSR, l4);
            //mv.visitInsn(RETURN);
            mv.visitJumpInsn(GOTO, l6);
            mv.visitLabel(l2);
            //}
            //catch (ex) {
            mv.visitLabel(l3);
            //store exception
            mv.visitVarInsn(ASTORE, 1);
            //handle exception
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            //finally
            mv.visitJumpInsn(JSR, l4);
            //return
            //mv.visitInsn(RETURN);
            mv.visitJumpInsn(GOTO, l6);

            //non catch exception
            mv.visitLabel(l5);
            mv.visitVarInsn(ASTORE, 1);
            //finally
            mv.visitJumpInsn(JSR, l4);
            //throw exception
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ATHROW);

            mv.visitLabel(l4);
            //finally address
            mv.visitVarInsn(ASTORE, 2);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("finally ... ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            //return from finally
            mv.visitVarInsn(RET, 2);

            mv.visitLabel(l6);
            mv.visitInsn(RETURN);
        }

        {
            /*
             * instruction lookupswitch
             *
             *  public void lookupSwitchIntIns(int t) {
                    switch (t) {
                        case 100:
                            System.out.println(t);
                            break;
                        case 0:
                            System.out.println(t);
                            break;
                        case -100:
                            System.out.println(t);
                            break;
                        default:
                            System.out.println(t);
                    }
                }
             */
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "lookupSwitchIntIns", "(I)V", null, null);

            Label l1 = new Label();
            Label l2 = new Label();
            Label l3 = new Label();
            Label l4 = new Label();
            Label l5 = new Label();


            mv.visitVarInsn(ILOAD, 1);
            mv.visitLookupSwitchInsn(l4, new int[] {100, 0, -100}, new Label[] {l1, l2, l3});

            //case 100:
            mv.visitLabel(l1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv.visitJumpInsn(GOTO, l5);

            //case 0:
            mv.visitLabel(l2);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv.visitJumpInsn(GOTO, l5);

            //case -100:
            mv.visitLabel(l3);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv.visitJumpInsn(GOTO, l5);

            //default:
            mv.visitLabel(l4);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv.visitJumpInsn(GOTO, l5);

            //return
            mv.visitLabel(l5);
            mv.visitInsn(RETURN);

            mv.visitLocalVariable("t", "I", null, l1, l5, 1);
            mv.visitEnd();
        }

        {
            /*
             *  synchronize block
             *
             *  public void synchronizeBlock2() {
                    synchronized (lock) {
                        System.out.println("synchronize block");
                    }
                }
             */
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "synchronizeBlock2", "()V", null, null);

            Label l1 = new Label();
            Label l2 = new Label();
            Label l3 = new Label();
            Label l4 = new Label();

            mv.visitTryCatchBlock(l1, l2, l2, null);
            mv.visitTryCatchBlock(l2, l3, l2, null);

            //get lock
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD ,"com/github/rodbate/it/asm/CompExam", "lock", "Ljava/lang/Object;");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, 1);

            //monitor enter
            mv.visitInsn(MONITORENTER);
            mv.visitLabel(l1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("synchronize block");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            //no crash and monitor exit
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(MONITOREXIT);
            mv.visitJumpInsn(GOTO, l4);

            //exception handler
            mv.visitLabel(l2);
            //store exception
            mv.visitVarInsn(ASTORE, 2);
            //monitor exit
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(MONITOREXIT);

            //rethrow ex
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ATHROW);

            //return
            mv.visitLabel(l4);
            mv.visitInsn(RETURN);

            mv.visitEnd();
        }

        cw.visitEnd();

        try (OutputStream os = Files.newOutputStream(Paths.get(".", "CompExam.class"))) {
            os.write(cw.toByteArray());
        } catch (IOException ignore) {
        }
    }

}
















