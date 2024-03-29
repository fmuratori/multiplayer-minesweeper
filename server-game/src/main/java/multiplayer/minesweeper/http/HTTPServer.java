/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package multiplayer.minesweeper.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import multiplayer.minesweeper.Controller;

import java.util.Map;

/**
 * HTTP server implementation. The server provides two Rest API endpoints for the creation of a
 * new Game and the available game modes. The specification of this API is visible at:
 * <a href="https://app.swaggerhub.com/apis-docs/fmuratori/multiplayer-minesweeper-game-service/1.0.0">Open API doc</a>
 */
public class HTTPServer extends AbstractVerticle {
    private final int port;

    public HTTPServer(Vertx vertx, int port) {
        this.port = port;
        vertx.deployVerticle(this);
    }

    private void createNewGame(RoutingContext rc) {
        rc.request().bodyHandler(bodyHandler -> {
            System.out.println("[HTTP Server] - Received POST new-game request. ");
            handleNewGameRequest(rc, bodyHandler);
        });
    }

    private void handleNewGameRequest(RoutingContext rc, Buffer bodyHandler) {
        JsonObject body;
        try {
            body = bodyHandler.toJsonObject();
        } catch (DecodeException e) {
            System.out.println("[HTTP Server] - New-game creation error. Body content decoding error.");
            rc.response()
                    .setStatusCode(400)
                    .end();
            return;
        }

        Controller.get().handleNewGameRequest(body.getString("name")).thenApply((Map<String, Object> object) -> {
            var status = (String) object.get("status");
            if (status.equals("GAME_MODE_ERROR")) {
                System.out.println("[HTTP Server] - Error occurred while handling a game creation request");
            } else if (status.equals("CREATED")) {
                var gameId = (String) object.get("gameId");
                rc.response()
                        .setStatusCode(200)
                        .putHeader("content-type",
                                "application/json; charset=utf-8")
                        .end(gameId);
            }
            return object;
        });
    }


    private void getGameModes(RoutingContext rc) {
        rc.request().bodyHandler(bodyHandler -> {
            System.out.println("[HTTP Server] - Received GET game-modes request. ");
            handleGameModesRequest(rc);
        });
    }

    private void handleGameModesRequest(RoutingContext rc) {
        Controller.get().handleGameModesRequest().thenApply((Map<String, Object> object) -> {
            var status = (String) object.get("status");
            if (!status.equals("LIST")) {
                System.out.println("[HTTP Server] - Error occurred while handling a game creation request");
            } else {
                JsonObject gameModes = new JsonObject()
                        .put("game_modes", object.get("gameModes"));
                rc.response()
                        .putHeader("content-type",
                                "application/json; charset=utf-8")
                        .end(gameModes.encode());
            }
            return object;
        });
    }

    /**
     * Starts the HTTP server.
     */
    @Override
    public void start() {
        Router router = Router.router(vertx);
        
        router.route().handler(
                CorsHandler.create(".*.")
                        .allowedMethod(HttpMethod.GET)
                        .allowedMethod(HttpMethod.POST)
                        .allowedHeader("Content-Type"));

        router.post("/new-game").handler(this::createNewGame);
        router.get("/game-modes").handler(this::getGameModes);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port)
                .onSuccess(server ->
                        System.out.println(
                                "[HTTP Server] - Http server started on port " + server.actualPort()
                        )
                );
    }
}