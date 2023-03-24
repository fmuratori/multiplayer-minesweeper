package multiplayer.minesweeper.websocket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import multiplayer.minesweeper.Controller;
import multiplayer.minesweeper.gameutils.GameMode;
import multiplayer.minesweeper.websocket.in.ActionMessage;
import multiplayer.minesweeper.websocket.in.JoinRoomMessage;
import multiplayer.minesweeper.websocket.in.LeaveRoomMessage;
import multiplayer.minesweeper.websocket.out.GameInfoMessage;
import multiplayer.minesweeper.websocket.out.GameOverMessage;
import multiplayer.minesweeper.websocket.out.GameUpdateMessage;
import multiplayer.minesweeper.websocket.out.PlayersCountMessage;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is dedicated to the management of a Socket.IO server which links the web applications
 * opened by players to the game core functionalities.
 *
 * Here are implemented the behaviours of message requests and responses (both broadcast and single channels responses)
 */
public class SocketServer {
    private final Configuration config;
    private final int port;
    private SocketIOServer server;

    public SocketServer(int port) {
        this.port = port;
        config = new Configuration();
        config.setPort(port);
    }

    /**
     * Initializes the server behaviours to execute when messages are received.
     */
    public void initialize() {
        server = new SocketIOServer(config);
        server.addConnectListener((client) -> System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Connected to socket"));
        server.addDisconnectListener(client -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Disconnected from socket");
            handleClientDisconnect(client);
        });
        server.addEventListener("action", ActionMessage.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleActionRequest(client, data);
        });
        server.addEventListener("join_room", JoinRoomMessage.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleJoinRoomRequest(client, data);
        });
        server.addEventListener("leave_room", LeaveRoomMessage.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleLeaveRoomRequest(client, data);

        });
        server.start();

        System.out.println("[Socket.IO] - SocketIO server started on port " + port);
    }

    private void handleClientDisconnect(SocketIOClient client) {
        UUID clientSessionId = client.getSessionId();
        Controller.get().handleClientDisconnect(clientSessionId).thenAccept((Map<String, Object> object) -> {
            var status = (String) object.get("status");
            String roomId = "";
            int connectedClients = 0;
            if (status.equals("USER_REMOVED")) {
                connectedClients = (int) object.get("connectedClients");
                roomId = (String) object.get("roomId");
            }

            switch (status) {
                case "USER_REMOVED":
                    System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Removed user from game");
                    server.getRoomOperations(roomId).sendEvent("players_count_update",
                            new PlayersCountMessage(connectedClients));
                case "GAME_DELETED":
                    System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Deleted game with no users connected");
                    break;
                case "GAME_NOT_FOUND":
                    System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Game for client " + client.getSessionId() + " not found");
            }
        });
    }

    private void handleActionRequest(SocketIOClient client, ActionMessage data) {
        Optional<String> roomId = client.getAllRooms().stream().filter((name) -> !name.equals("")).findFirst();
        if (roomId.isEmpty()) {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Room not found");
            return;
        }

        Controller.get().handleActionRequest(roomId.get(), data.getAction(), data.getxCoordinate(), data.getyCoordinate())
                .thenAccept((Map<String, Object> object) -> {
                    var status = (String) object.get("status");
                    if (status.equals("GAME_NOT_FOUND")) {
                        System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Game not found");
                    } else if (status.equals("EXECUTED")) {
                        var actionResult = (String) object.get("actionResult");
                        var map = (String) object.get("map");
                        long duration = 0;
                        if (actionResult.equals("GAME_OVER") || actionResult.equals("EXPLOSION"))
                            duration = (long) object.get("duration");
                        switch (actionResult) {
                            case "EXPLOSION":
                                server.getRoomOperations(roomId.get()).sendEvent("game_lost", new GameOverMessage(map, duration));
                                break;
                            case "GAME_OVER":
                                server.getRoomOperations(roomId.get()).sendEvent("game_won", new GameOverMessage(map, duration));
                                break;
                            case "OK":
                                server.getRoomOperations(roomId.get()).sendEvent("game_update", new GameUpdateMessage(map));
                                break;
                            case "IGNORED":
                                break;
                        }
                    }
                });
    }

    private void handleJoinRoomRequest(SocketIOClient client, JoinRoomMessage data) {
        Controller.get().handleJoinRoomRequest(data.getRoomName(), client.getSessionId()).thenAccept((Map<String, Object> object) -> {
            var status = (String) object.get("status");
            if (status.equals("GAME_NOT_FOUND")) {
                System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Game not found");
            } else if (status.equals("JOINED")) {
                System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Player joined room");
                var map = (String) object.get("map");
                var gameMode = (GameMode) object.get("gameMode");
                var startedAt = (String) object.get("startedAt");
                var playersCount = (int) object.get("playersCount");

                // if success
                client.joinRoom(data.getRoomName());
                client.sendEvent("game_info", new GameInfoMessage(map, gameMode, startedAt));

                // send new user connection to all connected users
                server.getRoomOperations(data.getRoomName()).sendEvent("players_count_update",
                        new PlayersCountMessage(playersCount));
            }
        });
    }

    private void handleLeaveRoomRequest(SocketIOClient client, LeaveRoomMessage data) {
        Controller.get().handleLeaveRoomRequest(data.getRoomName(), client.getSessionId()).thenAccept((Map<String, Object> object) -> {
            var status = (String) object.get("status");
            if (status.equals("GAME_NOT_FOUND")) {
                System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Game not found");
            } else if (status.equals("LEFT")) {
                var playersCount = (int) object.get("playersCount");
                client.leaveRoom(data.getRoomName());
                server.getRoomOperations(data.getRoomName()).sendEvent("players_count_update",
                        new PlayersCountMessage(playersCount));
            }
        });
    }
}

