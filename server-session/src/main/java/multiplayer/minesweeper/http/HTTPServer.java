package multiplayer.minesweeper.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import multiplayer.minesweeper.Controller;

import java.util.Map;

/**
 * HTTP server implementation. The server provides a simple Rest API endpoint for the creation of a
 * new Session. The specification of this API is visible at:
 * <a href="https://app.swaggerhub.com/apis-docs/fmuratori/multiplayer-minesweeper-session-service/1.0.0">Open API doc</a>
 */
public class HTTPServer extends AbstractVerticle {
    private final Router router;
    private final int port;

    public HTTPServer(Vertx vertx, int port) {
        this.port = port;
        // Create a Router
        router = Router.router(vertx);
        // Allow specific CORS request origins
        router.route().handler(
                CorsHandler.create(".*.")
                        .allowedMethod(HttpMethod.GET)
                        .allowedMethod(HttpMethod.POST));

        router.post("/new-session").handler(this::createNewSession);
        vertx.deployVerticle(this);
    }

    private void createNewSession(RoutingContext rc) {
        rc.request().bodyHandler(bodyHandler -> {
            final JsonObject body = bodyHandler.toJsonObject();
            System.out.println("[HTTP Server] - Received new-session request. Body: " + body);

            try {
                var sessionName = body.getString("name");
                var mode = body.getString("mode");
                var numPlayers = body.getInteger("numPlayers");
                var gridWidth = body.getInteger("gridWidth");
                var gridHeight = body.getInteger("gridHeight");

                if (sessionName.isBlank() || mode.isBlank()) {
                    throw new IllegalArgumentException();
                }

                Controller.get().handleNewSessionRequest(sessionName, mode, numPlayers, gridWidth, gridHeight).thenApply((Map<String,Object> result) -> {
                    JsonObject sessionJson = new JsonObject()
                            .put("roomId", result.get("roomId"))
                            .put("sessionName", sessionName);
                    rc.response()
                            .setStatusCode(200)
                            .putHeader("content-type",
                                    "application/json; charset=utf-8")
                            .end(sessionJson.encode());
                    return result;
                });
            } catch (Exception e) {
                rc.response()
                        .setStatusCode(400)
                        .putHeader("content-type",
                                "application/json; charset=utf-8")
                        .end();
            }
        });
    }

    @Override
    public void start() {
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port)
                .onSuccess(server ->
                        System.out.println(
                                "[HTTP Server] - HTTP server started on port " + server.actualPort()
                        )
                );
    }
}