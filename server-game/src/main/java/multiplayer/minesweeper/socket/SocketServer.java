package multiplayer.minesweeper.socket;

import com.corundumstudio.socketio.*;
import multiplayer.minesweeper.game.*;
import multiplayer.minesweeper.socket.in.ActionObject;
import multiplayer.minesweeper.socket.in.JoinRoomObject;
import multiplayer.minesweeper.socket.out.GameUpdateObject;
import multiplayer.minesweeper.socket.out.NewConnectionObject;

import java.util.*;

public class SocketServer {

    private static final SocketServer instance = new SocketServer();
    private SocketIOServer server;

    private SocketServer() {}

    public void initialize(int port) {
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(port);

        server = new SocketIOServer(config);
        server.addConnectListener((client) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Connected to socket");
        });
        server.addDisconnectListener(client -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Disconnected from socket");

            Optional<String> roomId = GamesManager.get().findGameByUser(client.getSessionId());
            if (roomId.isPresent()) {
                int connectedClients = server.getRoomOperations(roomId.get()).getClients().size();
                server.getRoomOperations(roomId.get()).sendEvent("players_count_update",
                        new NewConnectionObject(connectedClients));
                checkAndDeleteGame(roomId.get());
            }
        });

        server.addEventListener("action", ActionObject.class, (client, data, ackSender) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - " + data.toString());

            Optional<String> roomId = client.getAllRooms().stream().filter((name) -> !name.equals("")).findFirst();
            if (roomId.isEmpty())
                throw new IllegalStateException("Socket ID ["+client.getSessionId().toString()+"] Room not found");

            Game game = GamesManager.get().getGameInstance(roomId.get());

            ActionType requestedAction = ActionType.valueOf(data.getAction());
            ActionResult result = game.action(data.getxCoordinate(), data.getyCoordinate(), requestedAction);

            // broadcast messages to all clients
            String map = game.toString();
            switch (result) {
                case EXPLOSION:
                    server.getRoomOperations(roomId.get()).sendEvent("game_lost", new GameUpdateObject(map));
                    checkAndDeleteGame(roomId.get());
                    break;
                case GAME_OVER:
                    server.getRoomOperations(roomId.get()).sendEvent("game_won", new GameUpdateObject(map));
                    checkAndDeleteGame(roomId.get());
                    break;
                case OK:
                    server.getRoomOperations(roomId.get()).sendEvent("game_update", new GameUpdateObject(map));
                    break;
                case IGNORED:
                    break;
            }
        });
        server.addEventListener("join_room", JoinRoomObject.class, (client, data, ackSender) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - " + data.toString());
            // add user to one game sessione
            client.joinRoom(data.getRoomName());
            Game game = GamesManager.get().getGameInstance(data.getRoomName());
            // TODO: reject connection if a game with the specified roomName is not found (possible frontend page refresh)
            game.addPlayer(client.getSessionId());
            String map = game.toString();
            client.sendEvent("game_update", new GameUpdateObject(map));

            // send new user connection to all connected users
            int connectedClients = server.getRoomOperations(data.getRoomName()).getClients().size();
            server.getRoomOperations(data.getRoomName()).sendEvent("players_count_update",
                    new NewConnectionObject(connectedClients));
        });
        server.addEventListener("leave_room", JoinRoomObject.class, (client, data, ackSender) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - " + data.toString());
            // add user to one game sessione
            client.leaveRoom(data.getRoomName());
            GamesManager
                    .get()
                    .getGameInstance(data.getRoomName())
                    .removePlayer(client.getSessionId());
            // send new user connection to all connected users
            int connectedClients = server.getRoomOperations(data.getRoomName()).getClients().size();
            server.getRoomOperations(data.getRoomName()).sendEvent("players_count_update",
                    new NewConnectionObject(connectedClients));
        });
        server.start();

        System.out.println("SocketIO server started on port " + port);
    }

    private void checkAndDeleteGame(String roomId) {
        if (server.getRoomOperations(roomId).getClients().size() == 0) {
            GamesManager.get().deleteGame(roomId);
        }
    }

    public void stopServer() {
        server.stop();
    }

    public static SocketServer getInstance() {
        return instance;
    }

}

