package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
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
            out.println("you are the host! you can start the game when ever you want just send start to me!now please enter the number of players :");
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
                    out.println("Wrong format!");
                    out.flush();
                }
            }else if (this.host && messageFromClient.equals("start")){
                GameLogic.start = true;
            }
            else {
                out.println("Wrong format! or you are not the host! or the game is not start yet!");
                out.flush();

            }

        }
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }

    public void sendInfo(){
        sendMessage("your cards : " + hands.toString());
    }

    public void sendMessageToAll(String message){
        for (ClientHandler c :
                Server.clientHandlers) {
            c.sendMessage(message);
        }
    }
    public void play(int i){
        if (hands.contains((Object) i)){
            sendMessage("YOU PLAYED : " + i );
            sendMessageToAll("Player " + this.id + " Played : " + i);
            this.hands.remove((Object) i);
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
            Server.playersCount = Integer.parseInt(playerCount);
            out.println("done!");
            out.flush();
        }catch (Exception e){
            out.println("wrong format ! do you now how does numbers look like?");
            out.flush();
            defineCountOfPlayers();
        }
    }
    public void kill() throws IOException {
        socket.close();
    }
}
