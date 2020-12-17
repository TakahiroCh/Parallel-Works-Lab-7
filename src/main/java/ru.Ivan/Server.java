package ru.Ivan;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.*;
import org.zeromq.ZMsg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Server {
    final private static String CLIENT_SERVER = "tcp://localhost:7000";
    final private static String SERVER = "tcp://localhost:7777";
    final private static int TIMEOUT = 7000;
    final private static int CLIENT_SOCKET = 0;
    final private static String SPLIT = " ";

    private static Socket clientSocket;
    private static Socket serverSocket;
    private static Poller poller;
    private static ArrayList<Cache> caches;

    public static void main(String[] args) {
        ZContext cont = new ZContext(1);

        clientSocket = cont.createSocket(SocketType.ROUTER);
        clientSocket.bind(CLIENT_SERVER);

        serverSocket = cont.createSocket(SocketType.ROUTER);
        serverSocket.bind(SERVER);

        poller = cont.createPoller(2);
        poller.register(clientSocket, Poller.POLLIN);
        poller.register(serverSocket, Poller.POLLIN);

        caches = new ArrayList<Cache>();

        running();
    }

    private static void running() {
        long time = System.currentTimeMillis();
        while (poller.poll(TIMEOUT) != -1) {
            if (System.currentTimeMillis() - time >= TIMEOUT) {
                Collections.shuffle(caches);
                time = System.currentTimeMillis();
            }
            if (poller.pollin(CLIENT_SOCKET)) {
                ZMsg msg = ZMsg.recvMsg(clientSocket);
                String message = msg.getLast().toString().toLowerCase(Locale.ROOT);
                if (message.startsWith("get")) {
                    long key = Integer.parseInt(message.split(SPLIT)[1]);
                    boolean exists = false;
                    for (Cache c : caches) {
                        Boolean timeout = System.currentTimeMillis() - c.getTime() <= TIMEOUT
                        if (c.getStart() <= key && c.getFinish() >= key && timeout) {
                            c.
                        }
                    }
                }
            }
        }
    }

}
