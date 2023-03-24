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

/**
 * This class is the link between the network interfaces contained in the http and websocket packages and the core
 * components of the service. This class implements a Singleton design pattern and is accessible form any point
 * in the project.
 *
 * Internally, the Controller implements its logic using the Promise mechanism. Every request is handled
 * asynchronously from the caller and execution is demanded to a pool of worker threads.
 */
public class Controller {
    final static int SOCKET_SERVER_PORT = 8004;
    final static int HTTP_SERVER_PORT = 8003;
    private static final Controller instance = new Controller();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final GamesManager manager;

    private Controller() {
        manager = new GamesManager();
    }

    /**
     * Getter of the singleton instance of this class.
     * @return the Controller object
     */
    public static Controller get() {
        return instance;
    }

    /**
     * Method dedicated to the initialization of the web interfaces components, mainly an HTTP client, an http server
     * and a Socket.IO server.
     */
    public void initialize() {
        var socketServer = new SocketServer(SOCKET_SERVER_PORT);
        socketServer.initialize();
        var vertx = Vertx.vertx();
        new HTTPServer(vertx, HTTP_SERVER_PORT);
    }

    /**
     * Method called whenever an existing Socket.IO connection is lost. The user identified its id will be
     * removed from the corresponding game instance.
     *
     * @param userSessionId the users who losts the connections with this service
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
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
                    try {
                        game.removePlayer(userSessionId);
                        int connectedClients = game.getConnectedPlayersCount();
                        if (connectedClients == 0) {
                            output.put("status", "GAME_DELETED");
                            manager.deleteGame(roomId.get());
                        } else {
                            output.put("connectedClients", connectedClients);
                            output.put("roomId", roomId);
                            output.put("status", "USER_REMOVED");
                        }
                    } catch (IllegalArgumentException e) {
                        output.put("status", "DISCONNECT_ERROR");
                    }
                }
            }
            result.complete(output);
        });
        return result;
    }

    /**
     * Method called whenever a users requires the execution of an action on a game instance.
     *
     * @param roomId the game id. This id also matches with a Socket.IO room id
     * @param action the action requested by the player
     * @param xCoordinate the x coordinate at which apply the action
     * @param yCoordinate the y coordinate at witch apply the action
     * @return  a CompletableFuture object which will eventually provide the result of the request
     */
    public CompletableFuture<Map<String, Object>> handleActionRequest(String roomId, String action, int xCoordinate, int yCoordinate) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            ActionType requestedAction = null;
            try {
                requestedAction = ActionType.valueOf(action);
            } catch(IllegalArgumentException e) {
                output.put("status", "ACTION_ERROR");
                result.complete(output);
                return;
            }
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


    /**
     * This method implements the logic behind the join to a specific game instance by a player
     *
     * @param roomId the specific game id the users wants to join. This id matches with the Socket.IO room identifier.
     * @param clientSessionId the specific user id. This id matches with the user's Socket.IO channel id
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
    public CompletableFuture<Map<String, Object>> handleJoinRoomRequest(String roomId, UUID clientSessionId) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            var optGame = manager.getGameInstance(roomId);
            if (optGame.isEmpty()) {
                output.put("status", "GAME_NOT_FOUND");
            } else {
                try {
                    var game = optGame.get();
                    var map = game.toString();
                    game.addPlayer(clientSessionId);
                    output.put("status", "JOINED");
                    output.put("map", map);
                    output.put("startedAt", game.getStartedAt());
                    output.put("gameMode", game.getGameMode());
                    output.put("playersCount", game.getConnectedPlayersCount());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    output.put("status", "JOIN_ERROR");
                }
            }
            result.complete(output);
        });
        return result;
    }


    /**
     * This method implements the logic behind the leave of a specific game requested by a player.
     *
     * @param roomId the specific session id the users wants to join
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
    public CompletableFuture<Map<String, Object>> handleLeaveRoomRequest(String roomId, UUID clientSessionId) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            var optGame = manager.getGameInstance(roomId);
            if (optGame.isEmpty()) {
                output.put("status", "GAME_NOT_FOUND");
            } else {
                try {
                    var game = optGame.get();
                    game.removePlayer(clientSessionId);
                    output.put("status", "LEFT");
                    output.put("playersCount", game.getConnectedPlayersCount());
                } catch (IllegalArgumentException e) {
                    output.put("status", "LEAVE_ERROR");
                }
            }
            result.complete(output);
        });
        return result;

    }

    /**
     * This method is called whenever a game creation request is received from a HTTPServer object.
     *
     * @param gameModeName the game mode of the new game instance
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
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

    /**
     * This method is called whenever a players opens the web app main page and requests the list of available game
     * modes for the creation of a game.
     *
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
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
