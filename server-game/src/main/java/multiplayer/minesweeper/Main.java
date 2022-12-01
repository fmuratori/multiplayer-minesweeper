package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.server.HttpServer;
import multiplayer.minesweeper.socket.SocketServer;

public class Main {
    public static void main(String[] args) {

        // start socket.io server
        SocketServer.getInstance().initialize();

        // start http server
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpServer.getInstance());
    }
}
