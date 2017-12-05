package com.github.rodbate.fts;


import org.apache.lucene.LucenePackage;

public class Search {

    public static void main(String[] args) {

        Package pack = LucenePackage.get();
        System.out.println(pack.getName());
        System.out.println(pack.getImplementationTitle());
        System.out.println(pack.getImplementationVendor());
        System.out.println(pack.getImplementationVersion());
        System.out.println(pack.getSpecificationTitle());
        System.out.println(pack.getSpecificationVendor());
        System.out.println(pack.getSpecificationVersion());
    }
}
