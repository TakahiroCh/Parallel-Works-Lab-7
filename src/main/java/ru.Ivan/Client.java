package ru.Ivan;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.*;

import java.util.Scanner;

public class Client {
    final private static int TIMEOUT = 7000;
    final private static String CLIENT_SERVER = "tcp://localhost:7000";
    final private static boolean CYCLE = true;

    private static ZContext cont;
    private static Socket client;

    public static void main(String[] args) {
        cont = new ZContext(1);
        client = createClient();
        Scanner in = new Scanner(System.in);

        reading(in);
        cont.destroySocket(client);
        cont.destroy();
    }

    private static void reading(Scanner in) {
        while (CYCLE) {
            String cmd = in.nextLine();
            if (cmd.equals("stop")) break;
            client.send(cmd);
            String output = client.recvStr();
            if (output != null) {
                System.out.println(output);
            } else {
                System.out.println("No output");
                cont.destroySocket(client);
                client = createClient();
            }
        }
    }

    private static Socket createClient() {
        Socket socket = cont.createSocket(SocketType.REQ);
        socket.setReceiveTimeOut(TIMEOUT);
        socket.connect(CLIENT_SERVER);
        return socket;
    }
}
