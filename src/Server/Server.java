package Server;

import Client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Server {
    int port = 8080;
    static List<ClientHandler> clientHandlers;
    static List<ClientHandler> waiters;
    static int playersCount;
    static Socket botsSocket;

    public Server() {
        playersCount = 9;
        clientHandlers = new ArrayList<>();
        waiters = new ArrayList<>();
        init();
    }

    public void init() {
        boolean firstTime = true;
        System.out.println("Server is running...");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                if (firstTime) {
                    try {
                        botsSocket = new Socket("localhost", port);
                        Socket socket = serverSocket.accept();
                        System.out.println("BOTS SOCKET HAS BEEN MADE!");
                        firstTime = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    clientHandlers.clear();
                } else {
                    Socket socket = serverSocket.accept();
                    addNewClientHandler(socket);
                }

                System.out.println("====> There are " + clientHandlers.size() + " clients on the server!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewClientHandler(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(clientHandlers.size(), socket);
        String token = AuthToken.generateNewToken();
        clientHandler.setToken(token);
        clientHandler.sendMessage("token:"+token);
        if (!GameLogic.start && clientHandlers.size() <= playersCount) { //todo
            System.out.println("New connection accepted!");
            clientHandlers.add(clientHandler);
            new Thread(clientHandler).start();
            if (clientHandlers.size() == playersCount) {
                ClientHandler.checkForStartTheGame();
            }
        } else if (isThereAnyBotHere()){
            //todo replace client with bot
            clientHandler.sendMessage("game has been started ! i will replace you with bot (next round) if you want !");
            waiters.add(clientHandler);
            new Thread(clientHandler).start();
        }else {
            clientHandler.sendMessage("SERVER IS FULL !");
            clientHandler.kill();
        }

    }

    public static boolean isThereAnyBotHere() {

        for (ClientHandler c :
                clientHandlers) {
            if (c instanceof Bot) {
                return true;
            }
        }
        return false;
    }

    public static void startGame() {
       /* List<ClientHandler> cs = new ArrayList<>(clientHandlers);
        for (ClientHandler c:
             cs) {
            if (!c.isAlive()){
                clientHandlers.remove(c);
            }
        }*/
        GameLogic.start = true;


        while (clientHandlers.size() < playersCount) {
            try {
                ClientHandler bot = new Bot(clientHandlers.size(), botsSocket);
                new Thread(bot).start();
                clientHandlers.add(bot);
                System.out.println("BOT HAS BEEN MADE!");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("CAN NOT MAKE THAT BOT!");
            }
        }

        while (clientHandlers.size() > playersCount) {
            clientHandlers.get(clientHandlers.size() - 1).sendMessage("SORRY YOU ARE ADDITIONAL I HAVE TO REMOVE YOU BYE BYE !");
            try {
                clientHandlers.get(clientHandlers.size() - 1).kill();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientHandlers.remove(clientHandlers.size() - 1);
        }

        new GameLogic();
        ClientHandler.sendInfoToAll();
    }


}
