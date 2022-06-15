package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    Socket socket;
    private static String token;
    int port = 8080;
    String host = "localhost";
    Client(){

    }
    private void init() throws IOException {
        socket = new Socket(host, port);
        ClientSender clientSender = new ClientSender(this);
        ClientReceiver clientReceiver = new ClientReceiver(this);
        new Thread(clientReceiver).start();
        new Thread(clientSender).start();
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Client.token = token;
    }
}
