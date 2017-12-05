package com.github.rodbate.qotm.udp;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;

public class UdpServerMain {


    public static void main(String[] args) throws IOException {

        new Thread(new UpdServerExample()).start();

    }
}
