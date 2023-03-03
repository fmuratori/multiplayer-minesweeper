package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.rest.server.HTTPServer;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.rest.client.HTTPClient;
import multiplayer.minesweeper.socket.SocketServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws UnknownHostException {

        Vertx vertx = Vertx.vertx();

        // client for the communication between this server and server-game
        HTTPClient.newInstance(vertx, "mmgame", 8003);
        // HTTPClient restClient = new HTTPClient(vertx, "172.18.0.12", 8003);
        // HTTPClient restClient = new HTTPClient(vertx, "127.0.0.1", 8003);

        // start http server
        new HTTPServer(vertx, SessionsManager.get(), 8001);

        // start socket.io server
        SocketServer.get().initialize(SessionsManager.get(), 8002);
    }
}
