package com.github.rodbate.mappedbyte;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {

        RandomAccessFile ras = new RandomAccessFile("D:\\JJJ.txt", "rw");

        MappedByteBuffer byteBuffer = ras.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024 * 1024);

        int anInt = byteBuffer.getInt(0);

        System.out.println(anInt);
        System.out.println("--- mapped byte buffer size --- " + byteBuffer.capacity());


        Thread.sleep(1000 * 100000);

    }
}
