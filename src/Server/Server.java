package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final List<ClientHandler> clientHandlers;

    public Server() {
        clientHandlers = new ArrayList<>();
        init();
    }
    public void init() {
        System.out.println("Server is running...");
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                System.out.println("Waiting for a connection...");
                Socket socket = serverSocket.accept();
                addNewClientHandler(socket);
                System.out.println("====> There are " + clientHandlers.size() + " clients on the server!");

            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addNewClientHandler(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(clientHandlers.size(), socket);
        if (true) { //todo
            System.out.println("New connection accepted!");
            clientHandlers.add(clientHandler);
            new Thread(clientHandler).start();

        }

    }


}
