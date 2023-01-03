package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.rest.server.HTTPServer;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.cli.CLIActions;
import multiplayer.minesweeper.rest.client.HTTPClient;
import multiplayer.minesweeper.socket.SocketServer;

public class Main {
    public static void main(String[] args) {

        SessionsManager sessionsManager = new SessionsManager();
        Vertx vertx = Vertx.vertx();

        // client for the communication between this server and server-game
        HTTPClient restClient = new HTTPClient(vertx, "0.0.0.0", 8003);

        // start http server
        HTTPServer restServer = new HTTPServer(vertx, sessionsManager, 8001);

        // start socket.io server
        SocketServer.get().initialize(restClient, sessionsManager, 8002);

        new Thread(new CLIActions(restServer, restClient)).start();
    }
}
