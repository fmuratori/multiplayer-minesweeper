package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.cli.CLIActions;
import multiplayer.minesweeper.game.GamesManager;
import multiplayer.minesweeper.rest.HttpServer;
import multiplayer.minesweeper.socket.SocketServer;

public class Main {
    public static void main(String[] args) {

        GamesManager manager = new GamesManager();

        // start socket.io server
        SocketServer socketServer = new SocketServer(manager);
        socketServer.initialize(8004);

        // start http server
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = new HttpServer(vertx, 8003, manager);

        new Thread(new CLIActions(httpServer, socketServer)).start();
    }
}
