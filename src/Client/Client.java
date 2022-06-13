package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{


    private void init() throws IOException {
        Socket socket = new Socket("localhost", 8080);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        Scanner scanner = new Scanner(socket.getInputStream());
        Scanner scanner1 = new Scanner(System.in);
        while (true) {
            String messageToServer = scanner1.nextLine();
            printWriter.println(messageToServer);
            printWriter.flush();
            String input = scanner.nextLine();
            System.out.println("Message from server: " + input);
        }
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
