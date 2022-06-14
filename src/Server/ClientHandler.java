package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final int id;
    public String userName;
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
    // public boolean isAlive(){
    //    return !socket.isClosed();
    // }

    @Override
    public void run() {
        System.out.println("New ClientHandler is running...");
        sendMessage("ENTER YOUR USERNAME : ");
        userName = in.nextLine();
        sendMessage("if you wana play a card you should write your message in this way : play-CARDNUMBER || play-ninja");
        sendMessage("You are client number " + id + " and your user name is : " + userName);
        if (this.id == 0){
            this.host = true;
            sendMessage("you are the host! you can start the game when ever you want just send start to me!now please enter the number of players(maximum 9) :");
            defineCountOfPlayers();
        }
        while (true) {
            String messageFromClient = in.nextLine();
            System.out.println("Client "  + this.id + " <-> " + this.userName  + " : " + messageFromClient);
            if (checkOrderFormat(messageFromClient) && GameLogic.start){
                String[] order = messageFromClient.split("-");
                try {
                    play(Integer.parseInt(order[1]));
                }catch (Exception e){
                    if (order[1].equals("ninja")){
                        useNinjaCard();
                    }else {
                        e.printStackTrace();
                        sendMessage("Wrong format ! (make sure that you entered the numeric format)");
                    }

                }
            }else if (!GameLogic.start && this.host && messageFromClient.equals("start")){
                sendMessageToAll("GAME IS STARTED !");
                Server.startGame();
            }else if (checkEmoji(messageFromClient)){
                sendMessageToAll("Client " +  this.id + " <-> " + this.userName + " : " + messageFromClient);
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
        sendMessageToAll("Played cards : " + GameLogic.playedCards.toString() + " <-> LIVES : " + GameLogic.heartCard + " <-> NinjaCards : " + GameLogic.ninjaCard + " <-> LEVEL : " + GameLogic.levelCard);
    }
    public static void sendMessageToAll(String message){
        for (ClientHandler c :
                Server.clientHandlers) {
            c.sendMessage(message);
        }
    }
    public void play(int i){
        if (hands.contains((Object) i)){
            sendMessageToAll("Player " + this.id + " <-> " + this.userName + " Played : " + i);
            GameLogic.playedCards.add(i);
            Collections.sort(GameLogic.playedCards);
            this.hands.remove((Object) i);
            if (!checkMinimum()){
                sendMessageToAll("!!!WARN!!! ==> PLAYER "+ this.id+ " <-> " + this.userName + " JUST PLAYED A CARD THAT IT WASN'T MINIMUM AND YOU LUST A LIFE!");
                removeALife();
            }
            if (checkForNewRound()){
                GameLogic.startTheNewLevel();
                sendMessageToAll("!!!THE NEXT LEVEL IS STARTED!!!");
            }
            sendInfoToAll();
        }else {
            sendMessage("you dont have that card!");
        }
    }

    public boolean checkForNewRound() {
        boolean check = true;
        for (ClientHandler c :
                Server.clientHandlers) {
            if (!c.hands.isEmpty()) {
                check = false;
            }
        }
        return check;
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
                sendMessage("minimum is 2 maximum is 9 players! try again!");
                defineCountOfPlayers();
            }else {
                Server.playersCount = number;
                int i = number - Server.clientHandlers.size();
                if (i<=0){
                    checkForStartTheGame();
                }else {
                    sendMessage("done! we need " + i + " more players.");
                }
            }

        }catch (Exception e){
            sendMessage("wrong format ! do you now how does numbers look like?");
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
            killAll();
        }
    }

    public void useNinjaCard(){

        if (GameLogic.ninjaCard>0){
            GameLogic.ninjaCard--;
            sendMessageToAll("Player "+ this.id+ " <-> " + this.userName + " Used a ninja card !");
            for (ClientHandler c :
                    Server.clientHandlers) {
                if (!c.hands.isEmpty()){
                    GameLogic.playedCards.add(c.hands.get(0));
                    c.hands.remove(0);
                }
            }
            Collections.sort(GameLogic.playedCards);
            if (checkForNewRound()){
                GameLogic.startTheNewLevel();
                sendMessageToAll("!!!THE NEXT LEVEL IS STARTED!!!");
            }
            sendInfoToAll();
        }else {
            sendMessage("YOU DONT HAVE NINJA CARD !!!");
        }
    }
    public static Map<ClientHandler, Integer>  sortByValue(Map<ClientHandler, Integer> map){
        List<Map.Entry<ClientHandler, Integer>> list = new ArrayList(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        map = new LinkedHashMap();
        for(Map.Entry<ClientHandler, Integer> each : list) {
            map.put(each.getKey(), each.getValue());
        }
        return map;
    }

    public void kill() throws IOException {
        socket.close();
    }
    public static void killAll(){
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
