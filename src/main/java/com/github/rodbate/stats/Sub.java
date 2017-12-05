package com.github.rodbate.stats;






public class Sub extends Super {

    public final String msg;

    public Sub(String msg) {
        super(msg);
        this.msg = msg;
    }

    @Override
    public void over() {
        System.out.println(msg);
    }

    public static void main(String[] args) {
        new Sub("aaaa").over();
    }
}
