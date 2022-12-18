package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.game.GamesManager;
import multiplayer.minesweeper.server.HttpServer;
import multiplayer.minesweeper.socket.SocketServer;

public class Main {
    public static void main(String[] args) {
        GamesManager.getInstance().newGame(4, 4);
        // start socket.io server
        SocketServer.getInstance().initialize(8004);

        // start http server
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpServer.getInstance());
    }
}
