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
        String[] token = in.nextLine().split(":");
        Client.setToken(token[1]);
        System.out.println( "YOUR TOKEN IS : " + Client.getToken() );
        while (true) {
            String input = in.nextLine();
            System.out.println("server : " + input);
        }
    }
}
