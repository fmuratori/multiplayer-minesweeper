package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.cli.ListenActions;
import multiplayer.minesweeper.client.RestAPIClient;
import multiplayer.minesweeper.server.HttpServer;
import multiplayer.minesweeper.sessions.GameMode;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.socket.SocketServer;

public class Main {
    public static void main(String[] args) {
        // testing TODO: remove
        SessionsManager.getInstance().addSession("test_room_1", "Test session", GameMode.SMALL_GRID);
        SessionsManager.getInstance().addSession("test_room_2", "Test session 2", GameMode.MEDIUM_GRID);

        // start http server
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpServer.getInstance());

        // client for the communication between this server and server-game
        RestAPIClient client = new RestAPIClient(vertx);

        // start socket.io server
        SocketServer.getInstance().setGameClient(client);
        SocketServer.getInstance().initialize(8002);

        new Thread(new ListenActions()).start();

    }
}
