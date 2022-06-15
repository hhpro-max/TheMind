package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Bot extends ClientHandler{
    Random random;
    Bot(int id, Socket socket) throws IOException {
        super(id, socket);
        random = new Random();
        this.userName = "BOT" + id;
        host = false;
    }

    @Override
    public void run(){
        while (true) {
            if (GameLogic.start){
                try {
                    Thread.sleep(5000);//todo
                    Thread.sleep(waitingStrategy()* 1000L);
                    if (!hands.isEmpty()){
                        play(hands.get(0));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int waitingStrategy(){
        if (!this.hands.isEmpty()){
            int r1 = this.hands.get(0);
            int a = random.nextInt(5);
            int r2 = 0;
            if (!GameLogic.playedCards.isEmpty()){
                r2 = GameLogic.playedCards.get(GameLogic.playedCards.size()-1);
            }
            if (r1 - r2 < 5){
                if ((a / 10.0) * (r1-r2) < 2){
                    return (int) Math.round((a / 10.0) * (r1-r2));
                }else {
                    return 1;
                }
            }else if (r1 - r2 < 15){
                if ((a / 10.0) * (r1-r2) < 5 && (a / 10.0) * (r1-r2) > 3){
                    return (int) Math.round((a / 10.0) * (r1-r2));
                }else {
                    return 8;
                }
            }else if (r1 - r2 < 50){
                if ((a / 10.0) * (r1-r2) < 15 && (a / 10.0) * (r1-r2) > 8){
                    return (int) Math.round((a / 10.0) * (r1-r2));
                }else {
                    return 10;
                }
            }else {
                return 15;
            }
        }
        return 5;
    }
}
