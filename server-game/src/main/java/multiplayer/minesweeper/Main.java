package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.gameutils.GamesManager;
import multiplayer.minesweeper.rest.HttpServer;
import multiplayer.minesweeper.socket.SocketServer;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {

        GamesManager manager = new GamesManager();

        // start socket.io server
        SocketServer socketServer = new SocketServer(manager);
        socketServer.initialize(8004);

        // start http server
        Vertx vertx = Vertx.vertx();
        new HttpServer(vertx, 8003, manager);
    }
}
