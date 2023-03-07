package multiplayer.minesweeper.websocket;

import multiplayer.minesweeper.http.client.HTTPClient;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketController {

    // SessionsManager
    // HTTPClient


    private final ExecutorService executor = Executors.newCachedThreadPool();

    public CompletableFuture<Map<String, Object>> handleJoinSession(String roomName) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            Optional<Session> session = SessionsManager.get().getSession(roomName);
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
                    HTTPClient.get().sendGameRequest(roomName, session.get());
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

            Optional<Session> session = SessionsManager.get().getSession(roomName);
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
            result.complete(SessionsManager.get().getOpenSessions());
        });
        return result;
    }
}
