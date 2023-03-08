package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.http.HTTPClient;
import multiplayer.minesweeper.http.HTTPServer;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.websocket.SocketServer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {
    private static final int HTTP_SERVER_PORT = 8001;
    private static final int SOCKET_SERVER_PORT = 8002;
    private static final int GAME_SERVER_PORT = 8003;
    private static final String GAME_SERVER_HOST = "mmgame";
    private static final Controller instance = new Controller();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private SessionsManager sessionsManager;
    private HTTPClient httpClient;
    private SocketServer socketServer;

    private Controller() {
    }

    public static Controller get() {
        return instance;
    }

    public void initialize() {
        var vertx = Vertx.vertx();
        sessionsManager = new SessionsManager();
        httpClient = new HTTPClient(vertx, GAME_SERVER_HOST, GAME_SERVER_PORT);
        new HTTPServer(vertx, HTTP_SERVER_PORT);
        socketServer = new SocketServer(SOCKET_SERVER_PORT);
    }

    // HTTP client/server methods
    public void handleGameStarting(String sessionRoomName, String gameRoomName) {
        executor.execute(() -> {
            sessionsManager.getSession(sessionRoomName)
                    .ifPresent(value -> socketServer.sendGameStartingMessage(value, sessionRoomName, gameRoomName));
        });
    }

    public void handleGameStartingError(String sessionRoomName) {
        sessionsManager.removeSession(sessionRoomName);
        executor.execute(() -> socketServer.sendGameStartingError(sessionRoomName));
    }

    public CompletableFuture<String> handleNewSessionRequest(String sessionName, String mode, int numPlayers, int gridWidth, int gridHeight) {
        var result = new CompletableFuture<String>();
        executor.execute(() -> {
            var roomId = UUID.randomUUID().toString();
            var newSession = sessionsManager.addSession(roomId, sessionName, mode, numPlayers, gridWidth, gridHeight);

            if (numPlayers > 1)
                socketServer.emitSessionUpdate(newSession);

            result.complete(roomId);
        });
        return result;
    }


    // Socket.IO methods
    public CompletableFuture<Map<String, Object>> handleJoinSession(String roomName) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            var session = sessionsManager.getSession(roomName);
            if (session.isEmpty()) {
                output.put("status", "NO_SESSION");
            } else if (session.get().isFull()) {
                output.put("status", "FULL_SESSION");
            } else {
                session.get().addConnectedUsers();
                output.put("session", session.get());
                output.put("num_connections", session.get().getNumConnectedUsers());

                if (session.get().isFull()) {
                    // TODO: convert this call into a promise
                    httpClient.sendGameRequest(roomName, session.get());
                    output.put("status", "GAME_STARTING");
                } else {
                    output.put("status", "JOINED");
                }
            }
            result.complete(output);
        });
        return result;
    }

    public CompletableFuture<Map<String, Object>> handleLeaveSession(String roomName) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();

            Optional<Session> session = sessionsManager.getSession(roomName);
            if (session.isEmpty()) {
                output.put("status", "NO_SESSION");
            } else if (session.get().isEmpty()) {
                output.put("status", "EMPTY_SESSION");
            } else {
                session.get().removeConnectedUsers();
                output.put("session", session.get());
                output.put("status", "LEFT");
            }
            result.complete(output);
        });
        return result;
    }

    public CompletableFuture<List<Session>> handleNewConnection() {
        var result = new CompletableFuture<List<Session>>();
        executor.execute(() -> {
            result.complete(sessionsManager.getOpenSessions());
        });
        return result;
    }
}
