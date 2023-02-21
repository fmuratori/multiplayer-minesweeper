package multiplayer.minesweeper.rest.client;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.socket.SocketServer;

public class HTTPClient {
    private final WebClient client;
    private final int serverPort;
    private final String serverHost;

    public HTTPClient(Vertx vertx, String serverHost, int serverPort) {
        WebClientOptions options = new WebClientOptions();
        client = WebClient.create(vertx, options);
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

    public void sendGameRequest(String sessionRoomName, Session session) {
        client.post(serverPort, serverHost, "/new-game").sendJsonObject(
                        new JsonObject()
                                .put("name", session.getGameMode()))
                .onSuccess(response -> {
                    if (response.statusCode() == 200) {
                        System.out.println("[HTTP Client] - Received response with status code " + response.bodyAsString());
                        SocketServer.get().gameStartingResponse(sessionRoomName, response.bodyAsString());
                    } else {
                        SocketServer.get().gameStartingResponse(sessionRoomName, null);
                        System.out.println("[HTTP Client] - Error received on /new-game request. Status code: " + response.statusCode());
                    }
                })
                .onFailure(err -> {
                    SocketServer.get().gameStartingResponse(sessionRoomName, null);
                    System.out.println("[HTTP Client] - Unable to connect with the server. Error: " + err.getMessage());
                });
    }
}
