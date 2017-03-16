package com.github.rodbate.it;


import java.util.Objects;



public class Main {



    public int method1(String str){

        Objects.requireNonNull(str);
        return str.length();
    }

}
