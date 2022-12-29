package multiplayer.minesweeper.rest.client;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.socket.SocketServer;

public class RestClient {
    private static final String serverHost = "0.0.0.0";
    private static final int serverPort = 8003;

    private final WebClient client;
    public RestClient(Vertx vertx) {
        WebClientOptions options = new WebClientOptions()
//                .setUserAgent("My-App/1.2.3")
                ;
//        options.setKeepAlive(false);
        client = WebClient.create(vertx, options);
    }

    public void sendGameRequest(String sessionRoomName, Session session) {

        // Send a GET request
        client
            .post(serverPort, serverHost, "/new-game")
                .sendJsonObject(
                    new JsonObject()
                            .put("gridWidth", session.getGameMode().getGridWidth())
                            .put("gridHeight", session.getGameMode().getGridHeight())
                            .put("numPlayers", session.getGameMode().getNumPlayers())
                            .put("numConnectedPlayers", session.getNumConnectedUsers())
                            .put("minesPercentage", session.getGameMode().getMinesPercentage())
                )
            .onSuccess(response -> {
                if (response.statusCode() == 200) {
                    System.out.println("Received response with status code " + response.bodyAsString());
                    // run callback without stopping the http server
                    new Thread(() -> SocketServer
                            .getInstance()
                            .gameStartingResponse(sessionRoomName, response.bodyAsString()))
                            .start();


                } else {
                    System.out.println("Error received on /new-game request");
                }
            })
            .onFailure(err ->
                System.out.println("Something went wrong " + err.getMessage()));
    }
}
