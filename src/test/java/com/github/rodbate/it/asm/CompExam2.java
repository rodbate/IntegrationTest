package com.github.rodbate.it.asm;

/**
 *
 * Created by rodbate on 2018/1/25.
 */
public class CompExam2 {

    public static final String STR = "INIT";
    private final Object lock = new Object();

    public CompExam2() {
        System.out.println("<init>");
    }

    public static void loop() {
        for (int i = 0; i < 100; i++) {
            //loop
        }
    }

    private int add0(int a, int b) {
        int m = 100000000;
        return a + b;
    }

    public int add(int a, int b) {
        a = add0(a, b);
        System.out.println(new StringBuilder("super toString()").append(super.toString()));
        return a;
    }

    //instruction  lookupswitch
    public void lookupSwitchIntIns(int t) {
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

    //instruction  lookupswitch
    public void lookupSwitchStringIns(String t) {
        switch (t) {
            case "100":
                System.out.println(t);
                break;
            case "0":
                System.out.println(t);
                break;
            case "-100":
                System.out.println(t);
                break;
            default:
                System.out.println(t);
        }
    }



    public void tryCatchEx() {
        try {
            add(1, 2);
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void tryFinallyBlock() {
        try {
            add(1, 2);
        } finally {
            System.out.println("finally ... ");
        }
    }


    public void tryCatchFinallyBlock() {
        try {
            add(1, 2);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            System.out.println("finally ... ");
        }
    }


    public synchronized void synchronizeBlock(){
        System.out.println("synchronize block");
    }

    public void synchronizeBlock2() {
        synchronized (lock) {
            System.out.println("synchronize block");
        }
    }

    public void synchronizeBlock3(){
        synchronized (this) {
            System.out.println("synchronize block");
        }
    }

    public void synchronizeBlock4() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            System.out.println("synchronize block");
        }
    }
}
