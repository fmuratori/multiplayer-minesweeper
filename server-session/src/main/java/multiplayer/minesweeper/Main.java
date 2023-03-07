package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.http.server.HTTPServer;
import multiplayer.minesweeper.http.client.HTTPClient;
import multiplayer.minesweeper.websocket.SocketServer;

import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws UnknownHostException {

        Vertx vertx = Vertx.vertx();

        // client for the communication between this server and server-game
        HTTPClient.get().initialize(vertx, "mmgame", 8003);
        // HTTPClient restClient = new HTTPClient(vertx, "172.18.0.12", 8003);
        // HTTPClient restClient = new HTTPClient(vertx, "127.0.0.1", 8003);

        // start http server
        HTTPServer.get().initialize(vertx, 8001);

        // start socket.io server
        SocketServer.get().initialize(8002);
    }
}
