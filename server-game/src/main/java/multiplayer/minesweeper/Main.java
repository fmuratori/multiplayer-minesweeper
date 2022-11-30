package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.server.HttpServer;

public class Main {
    public static void main(String[] args) {

        // start socket.io server

        // start http server
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HttpServer());

    }
}
