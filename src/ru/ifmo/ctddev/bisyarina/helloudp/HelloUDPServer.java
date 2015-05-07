package ru.ifmo.ctddev.bisyarina.helloudp;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;

/**
 * Class provides functionality for multithread server using UDP
 */
public class HelloUDPServer implements HelloServer {
    private final static int BUF_SIZE = 10000;
    private DatagramSocket server;

    /**
     * Provides command line interface for starting server
     * @param args [port to start on, amount of threads to use]
     */
    public static void main(String[] args) {
        if (args == null) {
            System.out.println("Invalid args");
            return;
        }
        if (args.length < 2) {
            System.out.println("Too few args");
            return;
        }
        HelloUDPServer s = new HelloUDPServer();
        s.start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    /**
     * Starts server that
     * receives requests and responses with body of the request with "Hello, " prefix
     * @param port port to start server on
     * @param threads amount of threads server will use
     */
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

    /**
     * Terminates server
     */
    public void close() {
        if (server != null)
            server.close();
    }
}
