package com.github.rodbate.qotm.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MultiUdpClientMain {


    public static void main(String[] args) throws IOException {


        InetAddress group = InetAddress.getByName("230.0.0.0");

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    MulticastSocket client = new MulticastSocket(10000);
                    client.joinGroup(group);

                    while (true) {
                        byte[] data = new byte[256];
                        DatagramPacket receive = new DatagramPacket(data, data.length);
                        client.receive(receive);
                        if (receive.getLength() == 0) {
                            break;
                        }
                        System.err.printf("packet(%s -> %s, data[%s])\n",
                                receive.getAddress().getHostAddress() + ":" + receive.getPort(),
                                InetAddress.getLocalHost().getHostAddress() + ":" + client.getLocalPort(),
                                new String(data));
                    }

                    client.leaveGroup(group);
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        }

    }
}
