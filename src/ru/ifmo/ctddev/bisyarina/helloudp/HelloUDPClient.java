package ru.ifmo.ctddev.bisyarina.helloudp;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Class provides functionality for sending requests in multiple threads using UDP
 */
public class HelloUDPClient implements HelloClient {
    private final static int BUF_SIZE = 10020;
    private final static int TIMEOUT = 500;

    /**
     * Provides command line interface for running client
     * @param args [host to send requests, port to send requests, prefix to send,
     *             amount of requests per thread, amount of threads to use]
     */
    public static void main(String[] args) {
        if (args == null) {
            System.out.println("Invalid args");
            return;
        }
        if (args.length < 5) {
            System.out.println("Too few args");
            return;
        }
        HelloUDPClient s = new HelloUDPClient();
        s.start(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));

    }

    /**
     * Start client that
     * sends requests <prefix><number_of_thread>_<number_of_request_in_tread> until response with "Hello, " prefix
     * @param host host to send requests
     * @param port port to send reauests
     * @param prefix prefix of request
     * @param requests amount of request per thread
     * @param threads amount of threads
     */
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
                            byte[] bufRequest = msg.getBytes(Charset.forName("UTF-8"));
                            InetAddress hostAddr = InetAddress.getByName(host);
                            DatagramPacket reqPacket = new DatagramPacket(bufRequest, bufRequest.length, hostAddr, port);

                            byte[] bufResponse = new byte[BUF_SIZE];
                            DatagramPacket respPacket = new DatagramPacket(bufResponse, bufResponse.length);

                            client.send(reqPacket);
                            client.setSoTimeout(TIMEOUT);
                            client.receive(respPacket);
                            String result = new String(respPacket.getData(), respPacket.getOffset(),
                                    respPacket.getLength(), Charset.forName("UTF-8"));
                            if (result.equals("Hello, " + msg)) {
                                System.out.println(result);
                                j++;
                            }
                        } catch (IOException ignored) {
                        }
                    }
                });
                t[i].start();
            } catch (SocketException e) {
                System.err.println(e.getMessage());
            }
        }
        try {
            for (Thread aT : t) {
                aT.join();
            }
        } catch (InterruptedException ignored) {}
    }
}
