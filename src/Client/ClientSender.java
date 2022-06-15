package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSender implements Runnable{
    Socket socket;
    Scanner sendM;
    PrintWriter out;
    ClientSender(Client client) throws IOException {
        sendM = new Scanner(System.in);
        this.socket = client.socket;
        out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (true){
           String msg = sendM.nextLine();
           out.println(msg+":"+Client.getToken());
           out.flush();
        }
    }
}
