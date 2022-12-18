package multiplayer.minesweeper.socket;

import com.corundumstudio.socketio.*;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.socket.in.LeaveRoomObject;
import multiplayer.minesweeper.socket.in.JoinRoomObject;
import multiplayer.minesweeper.socket.out.*;

import java.util.List;
import java.util.Optional;

public class SocketServer {

    private static final SocketServer instance = new SocketServer();
    private SocketIOServer server;
    private SocketIONamespace browseNamespace;
    private SocketIONamespace sessionNamespace;

    private SocketServer() {}

    public void initialize(int port) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(port);

        server = new SocketIOServer(config);

        sessionNamespace = server.addNamespace("/session");
        sessionNamespace.addConnectListener((client) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Connected to namespace /session");
        });
        sessionNamespace.addDisconnectListener(client -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Disconnected from namespace /session");
        });
        sessionNamespace.addEventListener("join_room", JoinRoomObject.class, (client, data, ackSender) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - " + data.toString());

            String roomName = data.getRoomName();
            Optional<Session> session = SessionsManager.getInstance().getSession(roomName);
            if (session.isEmpty()) {
                sessionNamespace.getRoomOperations(roomName).sendEvent("session_error",
                        new SessionError("Session not found"));
            } else if (session.get().isFull()) {
                sessionNamespace.getRoomOperations(roomName).sendEvent("session_error",
                        new SessionError("Session is full"));
            } else {
                session.get().addConnectedUsers();

                // add user to one game sessione
                client.joinRoom(roomName);

                // send new user connection to all connected users
                int connectedClients = sessionNamespace.getRoomOperations(roomName).getClients().size();
                sessionNamespace.getRoomOperations(roomName).sendEvent("players_count_update",
                        new PlayersCountObject(connectedClients, session.get().getGameMode().getNumPlayers()));

                if (connectedClients == session.get().getGameMode().getNumPlayers()) {
                    System.out.println("Starting game for room " + roomName);
                    sessionNamespace.getRoomOperations(roomName).sendEvent("game_starting",
                            new GameStartingObject());
                    browseNamespace.getBroadcastOperations()
                            .sendEvent("session_update",
                                    new SessionUpdateObject(session.get(), "GAME_STARTING"));
                } else {
                    browseNamespace.getBroadcastOperations()
                            .sendEvent("session_update",
                                    new SessionUpdateObject(session.get(), "ADDED_USER"));
                }
            }
        });
        sessionNamespace.addEventListener("leave_room", LeaveRoomObject.class, (client, data, ackSender) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - " + data.toString());

            String roomName = data.getRoomName();
            Optional<Session> session = SessionsManager.getInstance().getSession(roomName);
            if (session.isEmpty()) {
                sessionNamespace.getRoomOperations(roomName).sendEvent("session_error",
                        new SessionError("Session not found"));
            } else if (session.get().isEmpty()) {
                sessionNamespace.getRoomOperations(roomName).sendEvent("session_error",
                        new SessionError("Session is empty"));
            } else {
                client.leaveRoom(roomName);

                session.get().removeConnectedUsers();

                // send new user connection to all connected users
                int connectedClients = server.getRoomOperations(roomName).getClients().size();
                server.getRoomOperations(roomName).sendEvent("players_count_update",
                        new PlayersCountObject(connectedClients, session.get().getGameMode().getNumPlayers()));

//                if (connectedClients == 0) {
//                    SessionsManager.getInstance().removeSession(roomName);
//                    browseNamespace.getBroadcastOperations()
//                            .sendEvent("session_update",
//                                    new SessionUpdateObject(session.get(), SessionUpdateType.CLOSED));
//                } else {
                    browseNamespace.getBroadcastOperations()
                            .sendEvent("session_update",
                                    new SessionUpdateObject(session.get(), "REMOVED_USER"));
//                }
            }
        });

        browseNamespace = server.addNamespace("/browse");
        browseNamespace.addConnectListener((client) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Connected to namespace /browse");
            client.sendEvent("sessions_update",
                    new SessionsUpdateObject(SessionsManager.getInstance().getAllSessions()));
        });
        browseNamespace.addDisconnectListener(client -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Disconnected from namespace /browse");
        });

        server.start();

        System.out.println("SocketIO server started on port " + port);
    }

    public void close() {
        System.out.println("Terminating Socket.IO server");
        server.stop();
    }

    public static SocketServer getInstance() {
        return instance;
    }

}

