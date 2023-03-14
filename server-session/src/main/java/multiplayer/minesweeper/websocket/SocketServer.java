package multiplayer.minesweeper.websocket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import multiplayer.minesweeper.Controller;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.websocket.in.JoinRoomMessage;
import multiplayer.minesweeper.websocket.in.LeaveRoomMessage;
import multiplayer.minesweeper.websocket.out.*;

import java.util.List;
import java.util.Map;

public class SocketServer {
    private final SocketIOServer server;
    private final SocketIONamespace browseNamespace;
    private final SocketIONamespace sessionNamespace;
    private final Controller controller;

    public SocketServer(int port) {
        Configuration config = new Configuration();
        config.setPort(port);
        controller = Controller.get();
        server = new SocketIOServer(config);

        // namespaces
        sessionNamespace = server.addNamespace("/session");
        browseNamespace = server.addNamespace("/browse");

        // handlers
        sessionNamespace.addConnectListener((client) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Connected to namespace /session");
        });

        sessionNamespace.addDisconnectListener(client -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Disconnected from namespace /session");
        });

        sessionNamespace.addEventListener("join_room", JoinRoomMessage.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleJoinRoomRequest(client, data);
        });

        sessionNamespace.addEventListener("leave_room", LeaveRoomMessage.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleLeaveRoomRequest(client, data);
        });

        browseNamespace.addConnectListener((client) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Connected to namespace /browse");
            handleBrowseConnection(client);
        });

        server.start();

        System.out.println("[Socket.IO] - SocketIO server started on port " + port);
    }

    private void handleBrowseConnection(SocketIOClient client) {
        controller.handleNewConnection().thenApply((List<Session> sessions) -> {
            client.sendEvent("sessions_update", new SessionsUpdateMessage(sessions));
            return sessions;
        });
    }

    private void handleLeaveRoomRequest(SocketIOClient client, LeaveRoomMessage data) {
        String roomName = data.getRoomName();
        controller.handleLeaveSession(roomName).thenApply((Map<String, Object> object) -> {
            String status = (String) object.get("status");
            Session session = (Session) object.get("session");
            switch (status) {
                case "NO_SESSION":
                    sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionErrorMessage("Session not found"));
                    break;
                case "FULL_SESSION":
                    sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionErrorMessage("Session is empty"));
                    break;
                case "LEFT":
                    client.leaveRoom(roomName);
                    int connectedClients = server.getRoomOperations(roomName).getClients().size();
                    server.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountMessage(connectedClients, session.getNumPlayers()));
                    browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateMessage(session, SessionUpdateType.REMOVED_USER));
            }
            return object;
        });
    }

    private void handleJoinRoomRequest(SocketIOClient client, JoinRoomMessage data) {
        String roomName = data.getRoomName();

        controller.handleJoinSession(roomName).thenApply((Map<String, Object> object) -> {
            var status = (String) object.get("status");
            Session session = null;
            var numMaxPlayers = 0;
            var numConnectedClients = 0;
            if (object.containsKey("session")) {
                session = (Session) object.get("session");
                numMaxPlayers = session.getNumPlayers();
                numConnectedClients = (int) object.get("num_connections");
            }

            switch (status) {
                case "NO_SESSION":
                    sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionErrorMessage("Session not found"));
                    break;
                case "FULL_SESSION":
                    sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionErrorMessage("Session is full"));
                    break;
                case "JOINED":
                    client.joinRoom(roomName);
                    sessionNamespace.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountMessage(numConnectedClients, numMaxPlayers));
                    browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateMessage(session, SessionUpdateType.ADDED_USER));
                    break;
                case "GAME_STARTING":
                    client.joinRoom(roomName);
                    sessionNamespace.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountMessage(numConnectedClients, numMaxPlayers));
                    browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateMessage(session, SessionUpdateType.GAME_STARTING));
                    break;
            }
            return object;
        });
    }

    public void sendGameStartingMessage(Session session, String sessionRoomName, String gameRoomName) {
        sessionNamespace
                .getRoomOperations(sessionRoomName)
                .sendEvent("game_starting", new GameStartingMessage(gameRoomName, session));
    }

    public void sendGameStartingError(String sessionRoomName) {
        sessionNamespace
                .getRoomOperations(sessionRoomName)
                .sendEvent("session_error", new SessionErrorMessage("Game server unreachable"));
    }

    public void emitSessionUpdate(Session session) {
        browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateMessage(session, SessionUpdateType.NEW_SESSION));
    }

    public void emitGameStartingFromTimer(Session session) {
        browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateMessage(session, SessionUpdateType.GAME_STARTING));
    }
}

