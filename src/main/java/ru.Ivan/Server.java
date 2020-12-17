package ru.Ivan;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.*;

public class Server {
    final private static String CLIENT_SERVER = "tcp://localhost:7000";
    final private static String SERVER = "tcp://localhost:7777";

    private static Socket clientSocket;
    private static Socket serverSocket;
    private static Poller poller;

    public static void main(String[] args) {
        ZContext cont = new ZContext(1);
        clientSocket = cont.createSocket(SocketType.ROUTER);
        clientSocket.bind(CLIENT_SERVER);
        serverSocket = cont.createSocket(SocketType.ROUTER);
        serverSocket.bind(SERVER);
        poller = cont.createPoller(2);
        poller.register(clientSocket, Poller.POLLIN);
        poller.register(serverSocket, Poller.POLLIN);

        long time = System.currentTimeMillis();




    }

}
