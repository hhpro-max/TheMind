package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final int id;
    List<Integer> hands;
    Scanner in;
    boolean host;

    ClientHandler(int id, Socket socket) throws IOException {
        this.socket = socket;
        this.id = id;
        out = new PrintWriter(socket.getOutputStream());
        hands = new ArrayList<>();
        in =  new Scanner(socket.getInputStream());
        host = false;
    }


    @Override
    public void run() {
        System.out.println("New ClientHandler is running...");
        out.println("if you wana play a card you should write your message in this way : play-CARDNUMBER");
        out.flush();

        sendMessage("You are client number " + id);
        if (this.id == 0){
            out.println("you are the host! you can start the game when ever you want just send start to me!now please enter the number of players(maximum 9) :");
            out.flush();
            defineCountOfPlayers();
            this.host = true;
        }
        while (true) {
            String messageFromClient = in.nextLine();
            System.out.println("Client " + this.id + " : " + messageFromClient);
            if (checkOrderFormat(messageFromClient) && GameLogic.start){
                String[] order = messageFromClient.split("-");
                try {
                    play(Integer.parseInt(order[1]));
                }catch (Exception e){
                    e.printStackTrace();
                    sendMessage("Wrong format ! (make sure that you entered the numeric format)");
                }
            }else if (!GameLogic.start && this.host && messageFromClient.equals("start")){
                sendMessageToAll("GAME IS STARTED !");
                Server.startGame();
            }else if (checkEmoji(messageFromClient)){
                sendMessageToAll("Client " + this.id + " : " + messageFromClient);
            }
            else {
                sendMessage("Wrong format ! XD ");
            }

        }
    }
    public boolean checkEmoji(String msg){
        if (msg.matches(".*[0-9].*")){
            return false;
        }else if (msg.split("").length > 2){
            return false;
        }
        return true;
    }
    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

    public void sendInfo(){
        sendMessage("your cards : " + hands.toString());
    }
    public static void sendInfoToAll(){
        for (ClientHandler c :
               Server.clientHandlers) {
            c.sendInfo();
        }
        sendMessageToAll("Played cards : " + GameLogic.playedCards.toString() + " <-> LIVES : " + GameLogic.heartCard + " <-> NinjaCards : " + GameLogic.ninjaCard);
    }
    public static void sendMessageToAll(String message){
        for (ClientHandler c :
                Server.clientHandlers) {
            c.sendMessage(message);
        }
    }
    public void play(int i){
        if (hands.contains((Object) i)){
            sendMessageToAll("Player " + this.id + " Played : " + i);
            GameLogic.playedCards.add(i);
            Collections.sort(GameLogic.playedCards);
            this.hands.remove((Object) i);
            if (!checkMinimum()){
                sendMessageToAll("!!!WARN!!! ==> PLAYER "+ this.id + " JUST PLAYED A CARD THAT IT WASN'T MINIMUM AND YOU LUST A LIFE!");
                removeALife();
            }
            sendInfoToAll();
        }else {
            out.println("you dont have that card!");
            out.flush();
        }
    }
    public boolean checkOrderFormat(String order){
        String[] orders = order.split("-");
        if (orders.length == 2 && orders[0].equals("play")){
            return true;
        }
        return false;
    }
    public void defineCountOfPlayers(){
        String playerCount = in.nextLine();
        try {
            int number = Integer.parseInt(playerCount);
            if (number > 9 || number < 2){
                out.println("minimum is 2 maximum is 9 players! try again!");
                out.flush();
                defineCountOfPlayers();
            }else {
                Server.playersCount = number;
                out.println("done!");
                out.flush();
            }

        }catch (Exception e){
            out.println("wrong format ! do you now how does numbers look like?");
            out.flush();
            defineCountOfPlayers();
        }
    }

    public boolean checkMinimum(){
       int checker = GameLogic.playedCards.get(GameLogic.playedCards.size()-1);
       boolean check = true;
        for (ClientHandler c:
             Server.clientHandlers) {
            if (!c.hands.isEmpty()){
                List<Integer> temporary = new ArrayList<>(c.hands);
                for (int i:
                     temporary) {
                    if (i < checker){
                        check = false;
                        c.hands.remove((Object) i);
                    }
                }
            }
        }
        return check;
    }

    public static void removeALife(){
        GameLogic.heartCard--;
        if (GameLogic.heartCard <= 0){
            sendMessageToAll("YOU LOST ! ( AND NOW I'M GONA KILL YOU ALL ! HAHA XD )");
            for (ClientHandler c:
                 Server.clientHandlers) {
                try {
                    c.kill();
                } catch (IOException e) {
                    e.printStackTrace();
                    //todo
                }
            }
        }
    }

    public void kill() throws IOException {
        socket.close();
    }
    public static void checkForStartTheGame(){
        sendMessageToAll("PLAYERS ARE READY ! WAITING FOR HOST !");
        for (ClientHandler c:
                Server.clientHandlers) {
            if (c.host){
                c.sendMessage("THE PLAYERS ARE READY DO YOU WANA START THE GAME ? (start)");
            }
        }
    }
}
