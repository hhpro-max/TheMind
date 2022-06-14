package Server;

import java.io.IOException;
import java.net.Socket;

public class Bot extends ClientHandler{

    Bot(int id, Socket socket) throws IOException {
        super(id, socket);
        this.userName = "BOT" + id;
        host = false;
    }
}
