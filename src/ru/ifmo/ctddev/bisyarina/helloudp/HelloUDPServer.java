package ru.ifmo.ctddev.bisyarina.helloudp;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;

public class HelloUDPServer implements HelloServer {
    final static int BUF_SIZE = 10000;
    DatagramSocket server;

    @Override
    public void start(int port, int threads) {
        try {
            server = new DatagramSocket(port);

            for (int i = 0; i < threads; i++) {
                new Thread(() -> {
                    while (!server.isClosed()) {
                        try {
                            byte[] buffer = new byte[BUF_SIZE];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            server.receive(packet);

                            String msg = "Hello, " + new String(packet.getData(), packet.getOffset(), packet.getLength());
                            byte[] bufResponse = msg.getBytes();
                            DatagramPacket packet1 = new DatagramPacket(bufResponse, bufResponse.length);
                            packet1.setSocketAddress(packet.getSocketAddress());

                            server.send(packet1);
                        } catch (IOException ignored) {}
                    }
                }).start();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (server != null)
            server.close();
    }
}
