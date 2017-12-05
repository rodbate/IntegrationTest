package com.github.rodbate.it;



import java.util.Objects;



public class Main {


    public static void main(String[] args) {

        String str = "aaa s fa ;";

        for (int i = 0; i < str.length(); i++) {
        }

    }



    public int method1(String str){

        Objects.requireNonNull(str);
        return str.length();
    }

}
