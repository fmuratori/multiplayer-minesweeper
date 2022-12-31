/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package multiplayer.minesweeper.rest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import multiplayer.minesweeper.game.GamesManager;

public class HttpServer extends AbstractVerticle {
    private final static HttpServer instance = new HttpServer();

    private HttpServer() {}

    private void createNewGame(RoutingContext rc) {

        rc.request().bodyHandler(bodyHandler -> {
            final JsonObject body = bodyHandler.toJsonObject();

            System.out.println("Received new-game request. Body: " + body);

            int gridWidth = body.getInteger("gridWidth");
            int gridHeight = body.getInteger("gridHeight");
            float minesPercentage = body.getFloat("minesPercentage");
            // number of players that joined the session
            int numConnectedPlayers = body.getInteger("numConnectedPlayers");
            // max number of allowed players inside a single session
            int numPlayers = body.getInteger("numPlayers");

            String gameId = GamesManager.get().newGame(gridWidth, gridHeight, minesPercentage);

            System.out.println("Created new-game, created Socketio room: " + gameId);

            rc.response()
                    .putHeader("content-type",
                            "application/json; charset=utf-8")
                    .end(gameId);
        });
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.post("/new-game").handler(this::createNewGame);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8003)
                .onSuccess(server ->
                        System.out.println(
                                "HTTP server started on port " + server.actualPort()
                        )
                );
    }

    public static HttpServer getInstance() {
        return instance;
    }
}