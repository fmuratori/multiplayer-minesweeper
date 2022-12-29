package multiplayer.minesweeper.socket;

import com.corundumstudio.socketio.*;
import multiplayer.minesweeper.rest.client.RestClient;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.socket.in.JoinRoomObject;
import multiplayer.minesweeper.socket.in.LeaveRoomObject;
import multiplayer.minesweeper.socket.out.*;

import java.util.Optional;

public class SocketServer {

    private static final SocketServer instance = new SocketServer();
    private SocketIOServer server;
    private SocketIONamespace browseNamespace;
    private SocketIONamespace sessionNamespace;
    private RestClient gameClient;

    private SocketServer() {}


    public void setGameClient(RestClient gameClient) {
        this.gameClient = gameClient;
    }

    public void initialize(int port) {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
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

                    gameClient.sendGameRequest(roomName, session.get());

                    browseNamespace.getBroadcastOperations()
                            .sendEvent("session_update",
                                    new SessionUpdateObject(session.get(), SessionUpdateType.GAME_STARTING));
                } else {
                    browseNamespace.getBroadcastOperations()
                            .sendEvent("session_update",
                                    new SessionUpdateObject(session.get(), SessionUpdateType.ADDED_USER));
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
                                    new SessionUpdateObject(session.get(), SessionUpdateType.REMOVED_USER));
//                }
            }
        });

        browseNamespace = server.addNamespace("/browse");
        browseNamespace.addConnectListener((client) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Connected to namespace /browse");
            client.sendEvent("sessions_update",
                    new SessionsUpdateObject(SessionsManager.getInstance().getOpenSessions()));
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

    public void gameStartingResponse(String sessionRoomName, String gameRoomName) {
        Optional<Session> session = SessionsManager.getInstance().getSession(sessionRoomName);
        session.ifPresent(value -> sessionNamespace
                .getRoomOperations(sessionRoomName)
                .sendEvent("game_starting",
                        new GameStartingObject(gameRoomName, value)));
    }

    public void emitSessionUpdate(Session session) {
        browseNamespace.getBroadcastOperations()
                .sendEvent("session_update",
                        new SessionUpdateObject(session, SessionUpdateType.NEW_SESSION));
    }

    public static SocketServer getInstance() {
        return instance;
    }

}

