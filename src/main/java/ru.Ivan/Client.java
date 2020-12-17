package ru.Ivan;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.*;

public class Client {
    final private static int TIMEOUT = 7000;
    final private static String CLIENT_SERVER = "tcp://localhost:7000";

    public static void main(String[] args) {
        ZContext cont = new ZContext(1);
        Socket client = cont.createSocket(SocketType.REQ);
        client.setReceiveTimeOut(TIMEOUT);
        client.connect(CLIENT_SERVER);

    }
}
