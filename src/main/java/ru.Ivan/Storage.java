package ru.Ivan;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.*;
import org.zeromq.ZMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public class Storage {
    final private static int TIMEOUT = 7000;
    final private static String SERVER = "tcp://localhost:7777";

    final private static String NOTICE = "notice";
    final private static long NOTICE_TIME = 1000;
    final private static String CACHE = "cache";
    final private static String PUT = "put";
    final private static String GET = "get";
    final private static String SPLIT = " ";

    final private static String id = UUID.randomUUID().toString();

    public static void main(String[] args) {
        ZContext cont = new ZContext(1);
        Socket socket = cont.createSocket(SocketType.DEALER);
        socket.connect(SERVER);
        Poller poller = cont.createPoller(1);
        poller.register(socket, Poller.POLLIN);

        ArrayList<String> caches = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        long start = Integer.parseInt(args[0]);
        long finish = start + caches.size() - 1;
        long time = System.currentTimeMillis();

        while (poller.poll(TIMEOUT) != -1) {
            long result = System.currentTimeMillis() - time;
            if (result >= NOTICE_TIME) {
                socket.send(NOTICE + " id: " + id + "; " + start + " -> " + finish);
                time = System.currentTimeMillis();
            }
            if (poller.pollin(0)) {
                ZMsg msg = ZMsg.recvMsg(socket);
                String message = msg.getLast().toString().toLowerCase(Locale.ROOT);
                if (msg.contains(GET)) {
                    try {
                        get(msg, message, caches, start);
                    } catch ()
                }
            }
        }
    }

    private static void get(ZMsg msg, String message, ArrayList<String> caches, long start) {
        long index = Integer.parseInt(message.split(SPLIT)[1]);
        msg.getLast().reset(CACHE + " " + caches.get(index - start));
    }
}
