package com.github.rodbate.qotm.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpClientMain {


    public static void main(String[] args) throws IOException, InterruptedException {


        DatagramSocket client = new DatagramSocket(0);

        while (true) {

            Thread.sleep(1000);

            DatagramPacket send = new DatagramPacket("saaaaa".getBytes(), 6, InetAddress.getLocalHost(), 8888);
            client.send(send);

            byte[] data = new byte[12];
            DatagramPacket receive = new DatagramPacket(data, data.length);
            client.receive(receive);
            System.err.printf("packet(%s -> %s, data[%s])\n",
                    receive.getAddress().getHostAddress() + ":" + receive.getPort(),
                    InetAddress.getLocalHost().getHostName() + ":" + client.getLocalPort(),
                    new String(data));
        }

        //client.close();
    }
}
