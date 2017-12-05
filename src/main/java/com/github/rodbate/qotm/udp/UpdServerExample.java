package com.github.rodbate.qotm.udp;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;


public class UpdServerExample implements Runnable {


    private volatile boolean hasMoreQuotes = true;

    private final DatagramSocket socket;
    private final BufferedReader reader;

    public UpdServerExample() throws IOException {
        this.socket = new DatagramSocket(8888);
        ClassLoader classLoader = getClass().getClassLoader();
        this.reader = new BufferedReader(new FileReader(classLoader.getResource("quotes.txt").getFile()));
    }

    @Override
    public void run() {

        while (hasMoreQuotes) {
            byte[] data = new byte[2];
            DatagramPacket receive = new DatagramPacket(data, data.length);
            try {
                /*socket.receive(receive);

                System.err.printf("packet(%s -> %s, data[%s])\n",
                        receive.getAddress().getHostName() + ":" + receive.getPort(),
                        InetAddress.getLocalHost().getHostName() + ":" + 8888,
                        new String(data));*/
                String nextLine = nextLine();
                String resp = nextLine == null ? "no more quotes !" : nextLine;
                InetAddress group = InetAddress.getByName("230.0.0.0");
                DatagramPacket send = new DatagramPacket(resp.getBytes(), resp.length(), group, 10000);
                socket.send(send);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        socket.close();
    }

    private String nextLine(){
        try {
            String s = reader.readLine();
            if (s == null) {
                hasMoreQuotes = false;
            }
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            hasMoreQuotes = false;
        }
        return null;
    }

}
