package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.rest.server.HTTPServer;
import multiplayer.minesweeper.sessions.GameMode;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.cli.CLIActions;
import multiplayer.minesweeper.rest.client.HTTPClient;
import multiplayer.minesweeper.socket.SocketServer;

public class Main {
    public static void main(String[] args) {

        SessionsManager sessionsManager = new SessionsManager();
        sessionsManager.addSession("test_room_1", "Small sized session", GameMode.SMALL_GRID);
        sessionsManager.addSession("test_room_2", "Medium sized session", GameMode.MEDIUM_GRID);
        sessionsManager.addSession("test_room_3", "Big sized session", GameMode.BIG_GRID);

        Vertx vertx = Vertx.vertx();

        // client for the communication between this server and server-game
        HTTPClient restClient = new HTTPClient(vertx, "0.0.0.0", 8003);

        // start http server
        HTTPServer restServer = new HTTPServer(vertx, sessionsManager, 8001);

        new Thread(new CLIActions(restServer, restClient)).start();

        // start socket.io server
        SocketServer.get().initialize(restClient, sessionsManager, 8002);


    }
}
