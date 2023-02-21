package multiplayer.minesweeper.socket;

import com.corundumstudio.socketio.*;
import multiplayer.minesweeper.rest.client.HTTPClient;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.socket.in.JoinRoomObject;
import multiplayer.minesweeper.socket.in.LeaveRoomObject;
import multiplayer.minesweeper.socket.out.*;

import java.util.Optional;

public class SocketServer {
    private static final SocketServer instance = new SocketServer();
    private SessionsManager sessionsManager;
    private SocketIOServer server;
    private SocketIONamespace browseNamespace;
    private SocketIONamespace sessionNamespace;
    private HTTPClient gameServerClient;

    private SocketServer() {

    }

    public static SocketServer get() {
        return instance;
    }

    public void initialize(HTTPClient restClient, SessionsManager manager, int port) {
        this.gameServerClient = restClient;
        this.sessionsManager = manager;

        Configuration config = new Configuration();
        config.setPort(port);

        server = new SocketIOServer(config);

        sessionNamespace = server.addNamespace("/session");
        sessionNamespace.addConnectListener((client) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Connected to namespace /session");
        });
        sessionNamespace.addDisconnectListener(client -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Disconnected from namespace /session");
        });
        sessionNamespace.addEventListener("join_room", JoinRoomObject.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());

            String roomName = data.getRoomName();
            Optional<Session> session = sessionsManager.getSession(roomName);
            if (session.isEmpty()) {
                sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session not found"));
            } else if (session.get().isFull()) {
                sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session is full"));
            } else {
                session.get().addConnectedUsers();

                // add user to one game sessione
                client.joinRoom(roomName);

                // send new user connection to all connected users
                int connectedClients = sessionNamespace.getRoomOperations(roomName).getClients().size();
                sessionNamespace.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountObject(connectedClients, session.get().getNumPlayers()));

                if (connectedClients == session.get().getNumPlayers()) {
                    System.out.println("[Socket.IO] - Starting game for room " + roomName);

                    gameServerClient.sendGameRequest(roomName, session.get());

                    browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session.get(), SessionUpdateType.GAME_STARTING));
                } else {
                    browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session.get(), SessionUpdateType.ADDED_USER));
                }
            }
        });
        sessionNamespace.addEventListener("leave_room", LeaveRoomObject.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());

            String roomName = data.getRoomName();
            Optional<Session> session = sessionsManager.getSession(roomName);
            if (session.isEmpty()) {
                sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session not found"));
            } else if (session.get().isEmpty()) {
                sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session is empty"));
            } else {
                client.leaveRoom(roomName);

                session.get().removeConnectedUsers();

                // send new user connection to all connected users
                int connectedClients = server.getRoomOperations(roomName).getClients().size();
                server.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountObject(connectedClients, session.get().getNumPlayers()));
                browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session.get(), SessionUpdateType.REMOVED_USER));
            }
        });

        browseNamespace = server.addNamespace("/browse");
        browseNamespace.addConnectListener((client) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Connected to namespace /browse");
            client.sendEvent("sessions_update", new SessionsUpdateObject(sessionsManager.getOpenSessions()));
        });
        browseNamespace.addDisconnectListener(client -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Disconnected from namespace /browse");
        });

        server.start();

        System.out.println("[Socket.IO] - SocketIO server started on port " + port);
    }

    public void close() {
        System.out.println("[Socket.IO] - Terminating Socket.IO server");
        server.stop();
    }

    public void gameStartingResponse(String sessionRoomName, String gameRoomName) {
        Optional<Session> session = sessionsManager.getSession(sessionRoomName);

        if (gameRoomName == null) {
            sessionsManager.removeSession(sessionRoomName);
            session.ifPresent(value ->
                    sessionNamespace
                            .getRoomOperations(sessionRoomName)
                            .sendEvent("session_error", new SessionError("Game server unreachable")));
        } else {
            session.ifPresent(value ->
                    sessionNamespace
                            .getRoomOperations(sessionRoomName)
                            .sendEvent("game_starting", new GameStartingObject(gameRoomName, value)));
        }
    }

    public void emitSessionUpdate(Session session) {
        browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session, SessionUpdateType.NEW_SESSION));
    }

}

