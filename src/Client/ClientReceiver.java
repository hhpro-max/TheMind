package Client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientReceiver implements Runnable {
    Socket socket;
    Scanner in;
    ClientReceiver(Client client) throws IOException {
        this.socket = client.socket;
        in = new Scanner(socket.getInputStream());
    }

    @Override
    public void run() {
        while (true) {
            String input = in.nextLine();
            System.out.println("Message from server: " + input);
        }
    }
}
