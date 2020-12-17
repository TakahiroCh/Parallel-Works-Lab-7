package ru.Ivan;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
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
    final private static int SERVER_SOCKET = 1;
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
                clientSocketRunning();
            }
            if (poller.pollin(SERVER_SOCKET)) {
                serverSocketRunning();
            }
        }
    }

    private static void serverSocketRunning() {
        ZMsg msg = ZMsg.recvMsg(serverSocket);
        ZFrame frame = msg.unwrap();
        String message = msg.getLast().toString().toLowerCase(Locale.ROOT);
        if (message.startsWith("notice")) {
            try {
                notice(msg, message, frame);
            } catch (Exception e){
                msg.getLast().reset("Exception");
                msg.send(clientSocket);
            }
        }
    }

    private static void notice(ZMsg msg, String message, ZFrame frame) throws Exception {
        String[] split = message.split(SPLIT);
        String id = split[1];
        long start = Integer.parseInt(split[2]);
        long finish = Integer.parseInt(split[3]);
        boolean existing = false;
        for (int i = 0; i < caches.size() + 1; i++) {
            if (i == caches.size()) {
                caches.add(new Cache(start, finish, id, fr))
            } else if (caches.get(i).getId().equals(id)) {
                caches.get(i).changeStart(start);
                caches.get(i).changeFinish(finish);
                caches.get(i).changeTime(System.currentTimeMillis());
            }
        }
    }

    private static void clientSocketRunning() {
        ZMsg msg = ZMsg.recvMsg(clientSocket);
        String message = msg.getLast().toString().toLowerCase(Locale.ROOT);
        if (message.startsWith("get")) {
            try {
                get(msg, message);
            } catch (Exception e) {
                msg.getLast().reset("Exception");
                msg.send(clientSocket);
            }
        } else if (message.startsWith("put")) {
            try {
                put(msg, message);
            } catch (Exception e) {
                msg.getLast().reset("Exception");
                msg.send(clientSocket);
            }
            msg.getLast().reset("Done...");
            msg.send(clientSocket);
        } else {
            msg.getLast().reset("Non-existing command");
            msg.send(clientSocket);
        }
    }

    private static void put(ZMsg msg, String message) throws Exception {
        String[] split = message.split(SPLIT);
        long key = Integer.parseInt(split[1]);
        String value = split[2];
        for (Cache c : caches) {
            if (c.getStart() <= key && c.getFinish() >= key) {
                c.getFrame().send(serverSocket, ZFrame.REUSE | ZFrame.MORE);
                msg.send(serverSocket, false);
            }
        }
    }

    private static void get(ZMsg msg, String message) throws Exception {
        long key = Integer.parseInt(message.split(SPLIT)[1]);
        boolean exists = false;
        for (Cache c : caches) {
            boolean timeout = System.currentTimeMillis() - c.getTime() <= TIMEOUT
            if (c.getStart() <= key && c.getFinish() >= key && timeout) {
                c.getFrame().send(serverSocket, ZFrame.REUSE | ZFrame.MORE);
                msg.send(serverSocket, false);
                exists = true;
                break;
            }
        }
        if (!exists) {
            msg.getLast().reset("Not existing");
            msg.send(clientSocket);
        }
    }

}
