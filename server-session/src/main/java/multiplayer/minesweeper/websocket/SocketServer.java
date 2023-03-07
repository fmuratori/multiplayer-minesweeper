package multiplayer.minesweeper.websocket;

import com.corundumstudio.socketio.*;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.websocket.in.JoinRoomObject;
import multiplayer.minesweeper.websocket.in.LeaveRoomObject;
import multiplayer.minesweeper.websocket.out.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SocketServer {
    private static final SocketServer instance = new SocketServer();
    private SocketIOServer server;
    private SocketIONamespace browseNamespace;
    private SocketIONamespace sessionNamespace;
    private SocketController controller;

    private SocketServer() {}

    public static SocketServer get() {
        return instance;
    }

    public void initialize(int port) {
        Configuration config = new Configuration();
        config.setPort(port);
        controller =  new SocketController();
        server = new SocketIOServer(config);

        // namepsaces
        sessionNamespace = server.addNamespace("/session");
        browseNamespace = server.addNamespace("/browse");

        // handlers
        sessionNamespace.addConnectListener((client) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Connected to namespace /session");
        });

        sessionNamespace.addDisconnectListener(client -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Disconnected from namespace /session");
        });

        sessionNamespace.addEventListener("join_room", JoinRoomObject.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            this.handleJoinRoomRequest(client, data);
        });

        sessionNamespace.addEventListener("leave_room", LeaveRoomObject.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleLeaveRoomRequest(client, data);
        });

        browseNamespace.addConnectListener((client) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Connected to namespace /browse");
            controller.handleNewConnection().thenApply((List<Session> sessions) -> {
                client.sendEvent("sessions_update", new SessionsUpdateObject(sessions));
                return sessions;
            });
        });

        server.start();

        System.out.println("[Socket.IO] - SocketIO server started on port " + port);
    }

    private void handleLeaveRoomRequest(SocketIOClient client, LeaveRoomObject data) {
        String roomName = data.getRoomName();
        controller.handleLeaveSession(roomName).thenApply((Map<String, Object> object) -> {
            String status = (String)object.get("status");
            Session session = (Session)object.get("session");
            switch (status) {
                case "NO_SESSION":
                    sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session not found"));
                    break;
                case "FULL_SESSION":
                    sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session is empty"));
                    break;
                case "LEFT":
                    client.leaveRoom(roomName);
                    int connectedClients = server.getRoomOperations(roomName).getClients().size();
                    server.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountObject(connectedClients, session.getNumPlayers()));
                    browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session, SessionUpdateType.REMOVED_USER));
            }
            return object;
        });
    }

    private void handleJoinRoomRequest(SocketIOClient client, JoinRoomObject data) {
        String roomName = data.getRoomName();

        controller.handleJoinSession(roomName).thenApply((Map<String, Object> object) -> {
            String status = (String)object.get("status");
            Session session = null;
            int numMaxPlayers = 0;
            int numConnectedClients = 0;
            if (object.containsKey("session")) {
                session = (Session)object.get("session");
                numMaxPlayers = session.getNumPlayers();
                numConnectedClients = (int)object.get("num_connections");
            }

            switch (status) {
                case "NO_SESSION":
                    sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session not found"));
                    break;
                case "FULL_SESSION":
                    sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session is full"));
                    break;
                case "JOINED":
                    client.joinRoom(roomName);
                    sessionNamespace.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountObject(numConnectedClients, numMaxPlayers));
                    browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session, SessionUpdateType.ADDED_USER));
                    break;
                case "GAME_STARTING":
                    client.joinRoom(roomName);
                    sessionNamespace.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountObject(numConnectedClients, numMaxPlayers));
                    browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session, SessionUpdateType.GAME_STARTING));
                    break;
            }
            return object;
        });
    }

    public void gameStartingResponse(String sessionRoomName, String gameRoomName) {
        Optional<Session> session = SessionsManager.get().getSession(sessionRoomName);

        if (gameRoomName == null) {
            SessionsManager.get().removeSession(sessionRoomName);
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

