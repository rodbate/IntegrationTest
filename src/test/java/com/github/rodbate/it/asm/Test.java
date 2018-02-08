package com.github.rodbate.it.asm;

public class Test {
    public static void main(String[] args) {
        printOne();
        printOne();
        printTwo();
        System.out.println((1f/3));
        System.out.println(100000.0/3);
    }
    
    public static void printOne() {
        System.out.println("Hello World");
    }
    
    public static void printTwo() {
        printOne();
        printOne();
    }
}
