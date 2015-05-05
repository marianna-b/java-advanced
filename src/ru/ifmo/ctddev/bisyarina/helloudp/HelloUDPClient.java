package ru.ifmo.ctddev.bisyarina.helloudp;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.util.Objects;

public class HelloUDPClient implements HelloClient {
    final static int BUF_SIZE = 10020;

    @Override
    public void start(String host, int port, String prefix, int requests, int threads) {
        Thread[] t = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            try {
                DatagramSocket client = new DatagramSocket();
                final int finalI = i;
                t[i] = new Thread(() -> {
                    int j = 0;
                    while (j < requests) {
                        try {
                            String msg = prefix + Integer.toString(finalI) + "_" + Integer.toString(j);
                            byte[] bufRequest = msg.getBytes();

                            InetAddress hostAddr = InetAddress.getByName(host);
                            DatagramPacket reqPacket = new DatagramPacket(bufRequest, bufRequest.length, hostAddr, port);

                            byte[] bufResponse = new byte[BUF_SIZE];
                            DatagramPacket respPacket = new DatagramPacket(bufResponse, bufResponse.length);

                            client.send(reqPacket);
                            client.setSoTimeout(500);
                            client.receive(respPacket);
                            String result = new String(respPacket.getData(), respPacket.getOffset(), respPacket.getLength());
                            if (Objects.equals(result, "Hello, " + msg)) {
                                System.out.println(result);
                                j++;
                            }
                        } catch (IOException ignored) {}
                    }
                });
                t[i].start();
            } catch (SocketException ignored) {
            }
        }
        try {
            for (Thread aT : t) {
                aT.join();
            }
        } catch (InterruptedException ignored) {}
    }
}
