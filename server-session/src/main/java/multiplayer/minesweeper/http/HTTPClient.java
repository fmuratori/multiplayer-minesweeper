package multiplayer.minesweeper.http;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import multiplayer.minesweeper.Controller;
import multiplayer.minesweeper.sessions.Session;

/**
 * Http client class. This class handles the request of a new game initialization from the
 * games service.
 */
public class HTTPClient {
    private final WebClient client;
    private final int serverPort;
    private final String serverHost;

    public HTTPClient(Vertx vertx, String serverHost, int serverPort) {
        var options = new WebClientOptions();
        client = WebClient.create(vertx, options);
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

    /**
     * Sends a new game request to the games service http server.
     *
     * @param sessionRoomName the session identifier
     * @param session the Session class instance that is ready to be started.
     */
    public void sendGameRequest(String sessionRoomName, Session session) {
        client.post(serverPort, serverHost, "/new-game").sendJsonObject(new JsonObject().put("name", session.getGameMode()))
                .onSuccess(response -> handleGameResponse(response.statusCode(), response.body(), sessionRoomName))
                .onFailure(error -> handleResponseError(error.getMessage(), sessionRoomName));
    }

    private void handleGameResponse(int statusCode, Buffer body, String sessionRoomName) {
        if (statusCode == 200) {
            System.out.println("[HTTP Client] - Received response with status code: " + statusCode);
            String gameRoomName = body.toString();
            Controller.get().handleGameStarting(sessionRoomName, gameRoomName);
        } else {
            System.out.println("[HTTP Client] - Error received on /new-game request. Status code: " + statusCode);
            Controller.get().handleGameStartingError(sessionRoomName);
        }
    }

    private void handleResponseError(String message, String sessionRoomName) {
        System.out.println("[HTTP Client] - Unable to connect with the server. Error: " + message);
        Controller.get().handleGameStartingError(sessionRoomName);
    }
}
