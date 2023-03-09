package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.game.ActionType;
import multiplayer.minesweeper.gameutils.GameModeFactory;
import multiplayer.minesweeper.gameutils.GamesManager;
import multiplayer.minesweeper.http.HTTPServer;
import multiplayer.minesweeper.websocket.SocketServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {
    final static int SOCKET_SERVER_PORT = 8004;
    final static int HTTP_SERVER_PORT = 8003;
    private static final Controller instance = new Controller();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private GamesManager manager;

    private Controller() {}

    public void initialize() {
        manager = new GamesManager();
        var socketServer = new SocketServer(SOCKET_SERVER_PORT);
        socketServer.initialize();
        var vertx = Vertx.vertx();
        new HTTPServer(vertx, HTTP_SERVER_PORT);
    }

    public CompletableFuture<Map<String, Object>> handleClientDisconnect(UUID userSessionId) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            var roomId = manager.findGameByUser(userSessionId);
            if (roomId.isEmpty()) {
                output.put("status", "GAME_NOT_FOUND");
            } else {
                var optGame = manager.getGameInstance(roomId.get());
                if (optGame.isEmpty()) {
                    output.put("status", "GAME_NOT_FOUND");
                } else {
                    var game = optGame.get();
                    game.removePlayer(userSessionId);
                    int connectedClients = game.getConnectedPlayersCount();
                    if (connectedClients == 0) {
                        output.put("status", "GAME_DELETED");
                        manager.deleteGame(roomId.get());
                    } else {
                        output.put("connectedClients", connectedClients);
                        output.put("roomId", roomId);
                        output.put("status", "GAME_DELETED");
                    }
                }
            }
            result.complete(output);
        });
        return result;
    }
    public static Controller get() {
        return instance;
    }

    public CompletableFuture<Map<String, Object>> handleActionRequest(String roomId, String action, int xCoordinate, int yCoordinate) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var requestedAction = ActionType.valueOf(action);
            var output = new HashMap<String, Object>();
            var optGame = manager.getGameInstance(roomId);
            if (optGame.isEmpty()) {
                output.put("status", "GAME_NOT_FOUND");
            } else {
                var game = optGame.get();
                var actionResult = game.action(xCoordinate, yCoordinate, requestedAction).toString();
                var map = game.toString();
                output.put("status", "EXECUTED");
                output.put("actionResult", actionResult);
                output.put("map", map);
                if (game.isOver()) {
                    output.put("duration", game.getDuration());
                }
            }
            result.complete(output);
        });
        return result;
    }

    public CompletableFuture<Map<String, Object>> handleJoinRoomRequest(String roomId, UUID clientSessionId) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            var optGame = manager.getGameInstance(roomId);
            if (optGame.isEmpty()) {
                output.put("status", "GAME_NOT_FOUND");
            } else {
                var game = optGame.get();
                var map = game.toString();
                game.addPlayer(clientSessionId);
                output.put("status", "JOINED");
                output.put("map", map);
                output.put("startedAt", game.getStartedAt());
                output.put("gameMode", game.getGameMode());
                output.put("playersCount", game.getConnectedPlayersCount());
            }
            result.complete(output);
        });
        return result;
    }

    public CompletableFuture<Map<String, Object>> handleLeaveRoomRequest(String roomId, UUID clientSessionId) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            var optGame = manager.getGameInstance(roomId);
            if (optGame.isEmpty()) {
                output.put("status", "GAME_NOT_FOUND");
            } else {
                var game = optGame.get();
                game.removePlayer(clientSessionId);
                output.put("status", "LEFT");
                output.put("playersCount", game.getConnectedPlayersCount());
            }
            result.complete(output);
        });
        return result;

    }

    public CompletableFuture<Map<String, Object>> handleNewGameRequest(String gameModeName) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            try {
                var gameMode = GameModeFactory.getByName(gameModeName);
                var gameId = manager.newGame(gameMode);
                output.put("status", "CREATED");
                output.put("gameId", gameId);
                System.out.println("[HTTP Server] - Created new-game, created Socket.IO room: " + gameId);
            } catch (IllegalArgumentException e) {
                output.put("status", "GAME_MODE_ERROR");
                System.out.println("[HTTP Server] - Game mode not found: " + gameModeName);
            }
            result.complete(output);
        });
        return result;
    }

    public CompletableFuture<Map<String, Object>> handleGameModesRequest() {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            try {
                output.put("status", "LIST");
                output.put("gameModes", GameModeFactory.getAllGameModes());
            } catch (Exception e) {
                output.put("status", "ERROR");
            }
            result.complete(output);
        });
        return result;
    }
}
