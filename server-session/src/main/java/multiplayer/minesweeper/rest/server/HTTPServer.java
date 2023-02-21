/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package multiplayer.minesweeper.rest.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.socket.SocketServer;

import java.util.UUID;

public class HTTPServer extends AbstractVerticle {

    private final SessionsManager sessionsManager;
    private final int port;

    public HTTPServer(Vertx vertx, SessionsManager manager, int port ) {
        this.sessionsManager = manager;
        this.port = port;
        vertx.deployVerticle(this);
    }

    private void createNewSession(RoutingContext rc) {
        rc.request().bodyHandler(bodyHandler -> {
            final JsonObject body = bodyHandler.toJsonObject();
            System.out.println("[HTTP Server] - Received new-session request. Body: " + body);

            try {
                String sessionName = body.getString("name");
                String mode = body.getString("mode");
                int numPlayers = body.getInteger("numPlayers");
                int gridWidth = body.getInteger("gridWidth");
                int gridHeight = body.getInteger("gridHeight");

                if (sessionName.isBlank() || mode.isBlank()) {
                    throw new IllegalArgumentException();
                }

                String roomId = UUID.randomUUID().toString();
                Session newSession = sessionsManager.addSession(roomId, sessionName, mode, numPlayers, gridWidth, gridHeight);
                SocketServer.get().emitSessionUpdate(newSession);

                rc.response()
                        .setStatusCode(200)
                        .putHeader("content-type",
                                "application/json; charset=utf-8")
                        .end();
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
        // Create a Router
        Router router = Router.router(vertx);

        // Allow specific CORS request origins
        router.route().handler(
                CorsHandler.create(".*.")
                        .allowedMethod(HttpMethod.GET)
                        .allowedMethod(HttpMethod.POST));

        router.post("/new-session").handler(this::createNewSession);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port)
                .onSuccess(server ->
                        System.out.println(
                                "[HTTP Server] - HTTP server started on port " + server.actualPort()
                        )
                );
    }

    public void close() {
        System.out.println("[HTTP Server] - Terminating Vert.X http server");
        vertx.close();
    }
}