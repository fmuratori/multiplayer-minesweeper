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

/**
 * Controller is the link between the network interfaces contained in the http and websocket packages and the core
 * components of the service. This class implements a Singleton design pattern and is accessible form any point
 * in the project.
 *
 * Internally, the Controller implements its logic using the Promise mechanism. Every request is handled
 * asynchronously from the caller and execution is demanded to a pool of worker threads.
 */
public class Controller {
    private static final int HTTP_SERVER_PORT = 8001;
    private static final int SOCKET_SERVER_PORT = 8002;
    private static final int GAME_SERVER_PORT = 8003;
    private static final String GAME_SERVER_HOST = "mmgame";
    private static final Controller instance = new Controller();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final SessionsManager sessionsManager;
    private HTTPClient httpClient;
    private SocketServer socketServer;

    private Controller() {
        sessionsManager = new SessionsManager();
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
        var vertx = Vertx.vertx();
        httpClient = new HTTPClient(vertx, GAME_SERVER_HOST, GAME_SERVER_PORT);
        new HTTPServer(vertx, HTTP_SERVER_PORT);
        socketServer = new SocketServer(SOCKET_SERVER_PORT);
    }

    /**
     * Method implements the logic behind the handling of a game starting message received from the game service
     * of the architecture. This method is called only after a successful response from the game server
     *
     * @param sessionRoomName the id of a specific session contained in the SessionsManager object
     * @param gameRoomName the id of a new game instance initialized by the games service
     */
    public void handleGameStarting(String sessionRoomName, String gameRoomName) {
        executor.execute(() -> sessionsManager.getSession(sessionRoomName)
                .ifPresent(value -> socketServer.emitGameStartingMessage(value, sessionRoomName, gameRoomName)));
    }

    /**
     * Method implements the logic behind the handling of a game starting message received from the game service
     * of the architecture. This method is called only after an error response from the game server
     *
     * @param sessionRoomName the id of a specific session contained in the SessionsManager object
     */
    public void handleGameStartingError(String sessionRoomName) {
        executor.execute(() -> {
            sessionsManager.removeSession(sessionRoomName);
            socketServer.emitGameStartingError(sessionRoomName);
        });
    }

    /**
     * This method implements the creation of a new session. The request comes from the HTTPServer class which
     * exposes a REST API endpoint.
     *
     * @param sessionName the custom name provided by the user
     * @param mode the game mode name identifier
     * @param numPlayers the number of players linked to the game mode
     * @param gridWidth the width of the game grid
     * @param gridHeight the height of the game grid
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
    public CompletableFuture<Map<String, Object>> handleNewSessionRequest(String sessionName, String mode, int numPlayers, int gridWidth, int gridHeight) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            var roomId = UUID.randomUUID().toString();
            output.put("status", "CREATED");
            output.put("roomId", roomId);

            var newSession = sessionsManager
                    .addSession(roomId, sessionName, mode, numPlayers, gridWidth, gridHeight);
            if (numPlayers > 1 &&  socketServer != null)
                socketServer.emitSessionUpdate(newSession);

            result.complete(output);
        });
        return result;
    }

    /**
     * This method implements the logic behind the join of a specific session requested by a player. Note that no
     * information about the player are provided.
     *
     * @param roomId the specific session id the users wants to join
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
    public CompletableFuture<Map<String, Object>> handleJoinSession(String roomId) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();
            var session = sessionsManager.getSession(roomId);
            if (session.isEmpty()) {
                output.put("status", "NO_SESSION");
            } else if (session.get().isFull()) {
                output.put("status", "FULL_SESSION");
            } else {
                session.get().addConnectedUsers();
                output.put("session", session.get());
                output.put("num_connections", session.get().getNumConnectedUsers());

                if (session.get().checkStartCondition()) {
                    if (httpClient != null)
                        httpClient.sendGameRequest(roomId, session.get());
                    output.put("status", "GAME_STARTING");
                } else {
                    output.put("status", "JOINED");
                }
            }
            result.complete(output);
        });
        return result;
    }

    /**
     * Method called when a wating room condition is verified and a game can start. Here the games service is
     * called through a REST API call generated inside the HTTPClient object. Also, all the users connected to
     * the socket session must be updated.
     *
     * @param roomName the socket room id used to identify all the users of a specific session
     * @param session the session instance which contains additional info about the game configuration
     */
    public void sendGameStartingRequest(String roomName, Session session) {
        httpClient.sendGameRequest(roomName, session);
        socketServer.emitGameStartingFromTimer(session);
    }

    /**
     * This method implements the logic behind the leave of a specific session requested by a player. Note that no
     * information about the player are provided.
     *
     * @param roomId the specific session id the users wants to join
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
    public CompletableFuture<Map<String, Object>> handleLeaveSession(String roomId) {
        var result = new CompletableFuture<Map<String, Object>>();
        executor.execute(() -> {
            var output = new HashMap<String, Object>();

            Optional<Session> session = sessionsManager.getSession(roomId);
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

    /**
     * This method implements the logic behind the detection of a new socke.io connection, generated automatically
     * as soon as a player connects to this service. Firstly, as soon as a new player opens the web page
     * he is updated of the current open sessions he can join.
     *
     * @return a CompletableFuture object which will eventually provide the result of the request
     */
    public CompletableFuture<List<Session>> handleNewConnection() {
        var result = new CompletableFuture<List<Session>>();
        executor.execute(() -> result.complete(sessionsManager.getOpenSessions()));
        return result;
    }
}
