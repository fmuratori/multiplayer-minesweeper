package multiplayer.minesweeper.websocket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import multiplayer.minesweeper.Controller;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.websocket.in.JoinRoomObject;
import multiplayer.minesweeper.websocket.in.LeaveRoomObject;
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
            handleJoinRoomRequest(client, data);
        });

        sessionNamespace.addEventListener("leave_room", LeaveRoomObject.class, (client, data, ackSender) -> {
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
            client.sendEvent("sessions_update", new SessionsUpdateObject(sessions));
            return sessions;
        });
    }

    private void handleLeaveRoomRequest(SocketIOClient client, LeaveRoomObject data) {
        String roomName = data.getRoomName();
        controller.handleLeaveSession(roomName).thenApply((Map<String, Object> object) -> {
            String status = (String) object.get("status");
            Session session = (Session) object.get("session");
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

    public void sendGameStartingMessage(Session session, String sessionRoomName, String gameRoomName) {
        sessionNamespace
                .getRoomOperations(sessionRoomName)
                .sendEvent("game_starting", new GameStartingObject(gameRoomName, session));
    }

    public void sendGameStartingError(String sessionRoomName) {
        sessionNamespace
                .getRoomOperations(sessionRoomName)
                .sendEvent("session_error", new SessionError("Game server unreachable"));
    }

    public void emitSessionUpdate(Session session) {
        browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session, SessionUpdateType.NEW_SESSION));
    }

}

