package Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GameLogic {
    static Stack<Integer> cards;
    static int levelCard;
    static int heartCard;
    static int ninjaCard;
    static boolean start = false;
    static List<Integer> playedCards;
    GameLogic(){
        cards = new Stack<>();
        playedCards = new ArrayList<>();
        initCards();
        levelCard = 1;
        heartCard = Server.clientHandlers.size();
        ninjaCard = 2;
        initClientHands();
    }
    public static void initCards(){
        cards.clear();
        for (int i = 1;i <= 100;i++){
            cards.add(i);
        }
        Collections.shuffle(cards);
    }
    public static void initClientHands(){
        for (ClientHandler c :
                Server.clientHandlers) {
            for (int i = 0;i < levelCard;i++){
                c.hands.add(cards.pop());
            }
            Collections.sort(c.hands);
        }
    }
    public static void startTheNewLevel(){
        levelCard++;
        playedCards.clear();
        if (levelCard==3||levelCard==6||levelCard==9){
            heartCard++;
        }
        if (levelCard==2||levelCard==5||levelCard==8){
            ninjaCard++;
        }
        if (levelCard == 13){
            ClientHandler.sendMessageToAll("     !<===>! YOU WON !<===>!  ");
            ClientHandler.killAll();
        }
        initCards();
        initClientHands();
    }


}
