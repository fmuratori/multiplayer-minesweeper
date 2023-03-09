package multiplayer.minesweeper.websocket;

import com.corundumstudio.socketio.*;
import multiplayer.minesweeper.Controller;
import multiplayer.minesweeper.gameutils.GameMode;
import multiplayer.minesweeper.websocket.in.ActionObject;
import multiplayer.minesweeper.websocket.in.JoinRoomObject;
import multiplayer.minesweeper.websocket.out.GameInfoObject;
import multiplayer.minesweeper.websocket.out.GameOverObject;
import multiplayer.minesweeper.websocket.out.GameUpdateObject;
import multiplayer.minesweeper.websocket.out.NewConnectionObject;

import java.util.*;

public class SocketServer {
    private final Configuration config;
    private final int port;
    private SocketIOServer server;

    public SocketServer(int port) {
        this.port = port;
        config = new Configuration();
        config.setPort(port);
    }

    public void initialize() {
        server = new SocketIOServer(config);
        server.addConnectListener((client) -> System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Connected to socket"));
        server.addDisconnectListener(client -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - Disconnected from socket");
             handleClientDisconnect(client);
        });
        server.addEventListener("action", ActionObject.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleActionRequest(client, data);
        });
        server.addEventListener("join_room", JoinRoomObject.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleJoinRoomRequest(client, data);
        });
        server.addEventListener("leave_room", JoinRoomObject.class, (client, data, ackSender) -> {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());
            handleLeaveRoomRequest(client, data);

        });
        server.start();

        System.out.println("[Socket.IO] - SocketIO server started on port " + port);
    }

    private void handleClientDisconnect(SocketIOClient client) {
        UUID clientSessionId = client.getSessionId();
        Controller.get().handleClientDisconnect(clientSessionId).thenApply((Map<String, Object> object) -> {
            var status = (String) object.get("status");
            String roomId = "";
            int connectedClients = 0;
            if (status.equals("USER_REMOVED")) {
                connectedClients = (int)object.get("connectedClients");
                roomId = (String)object.get("roomId");
            }

            switch (status) {
                case "USER_REMOVED":
                    System.out.println("[Socket.IO] - Socket ID [\" + client.getSessionId().toString() + \"] Removed user from game");
                    server.getRoomOperations(roomId).sendEvent("players_count_update",
                            new NewConnectionObject(connectedClients));
                case "GAME_DELETED":
                    System.out.println("[Socket.IO] - Socket ID [\" + client.getSessionId().toString() + \"] Deleted game with no users connected");
                    break;
                case "GAME_NOT_FOUND":
                    System.out.println("[Socket.IO] - Socket ID [\" + client.getSessionId().toString() + \"] Game for client " + client.getSessionId() + " not found" );
            }

            return object;
        });
    }
    private void handleActionRequest(SocketIOClient client, ActionObject data) {
        Optional<String> roomId = client.getAllRooms().stream().filter((name) -> !name.equals("")).findFirst();
        if (roomId.isEmpty()) {
            System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] Room not found");
            return;
        }

        Controller.get().handleActionRequest(roomId.get(), data.getAction(), data.getxCoordinate(), data.getyCoordinate())
                .thenApply((Map<String, Object> object) -> {
            var status = (String)object.get("status");
            if (status.equals("GAME_NOT_FOUND")) {
                System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] Game not found");
            } else if (status.equals("EXECUTED")) {
                var actionResult = (String) object.get("actionResult");
                var map = (String) object.get("map");
                long duration = 0;
                if (actionResult.equals("GAME_OVER") || actionResult.equals("EXPLOSION"))
                    duration = (long) object.get("duration");
                switch (actionResult) {
                    case "EXPLOSION":
                        server.getRoomOperations(roomId.get()).sendEvent("game_lost", new GameOverObject(map, duration));
                        break;
                    case "GAME_OVER":
                        server.getRoomOperations(roomId.get()).sendEvent("game_won", new GameOverObject(map, duration));
                        break;
                    case "OK":
                        server.getRoomOperations(roomId.get()).sendEvent("game_update", new GameUpdateObject(map));
                        break;
                    case "IGNORED":
                        break;
                }
            }
            return object;
        });
    }
    private void handleJoinRoomRequest(SocketIOClient client, JoinRoomObject data) {
        Controller.get().handleJoinRoomRequest(data.getRoomName(), client.getSessionId()).thenApply((Map<String, Object> object) -> {
            var status = (String)object.get("status");
            if (status.equals("GAME_NOT_FOUND")) {
                System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] Game not found");
            } else if (status.equals("JOINED")) {
                System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] Player joined room");
                var map = (String)object.get("map");
                var gameMode = (GameMode)object.get("gameMode");
                var startedAt = (String)object.get("startedAt");
                var playersCount = (int)object.get("playersCount");

                // if success
                client.joinRoom(data.getRoomName());
                client.sendEvent("game_info", new GameInfoObject(map, gameMode, startedAt));

                // send new user connection to all connected users
                server.getRoomOperations(data.getRoomName()).sendEvent("players_count_update",
                        new NewConnectionObject(playersCount));
            }
            return object;
        });
    }
    private void handleLeaveRoomRequest(SocketIOClient client, JoinRoomObject data) {
        Controller.get().handleLeaveRoomRequest(data.getRoomName(), client.getSessionId()).thenApply((Map<String, Object> object) -> {
            var status = (String)object.get("status");
            if (status.equals("GAME_NOT_FOUND")) {
                System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] Game not found");
            } else if (status.equals("LEFT")) {
                var playersCount = (int)object.get("playersCount");
                client.leaveRoom(data.getRoomName());
                server.getRoomOperations(data.getRoomName()).sendEvent("players_count_update",
                        new NewConnectionObject(playersCount));
            }
            return object;
        });
    }
}

