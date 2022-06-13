package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    Socket socket;
    Client(){

    }
    private void init() throws IOException {
        socket = new Socket("localhost", 8080);
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
}
