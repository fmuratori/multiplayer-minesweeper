package multiplayer.minesweeper.socket;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.EventInterceptor;
import com.corundumstudio.socketio.transport.NamespaceClient;
import multiplayer.minesweeper.game.*;
import multiplayer.minesweeper.socket.in.ActionObject;
import multiplayer.minesweeper.socket.in.JoinRoomObject;
import multiplayer.minesweeper.socket.in.MessageObject;
import multiplayer.minesweeper.socket.out.GameUpdateObject;
import multiplayer.minesweeper.socket.out.NewConnectionObject;

import java.util.*;

public class SocketServer {

    private static final SocketServer instance = new SocketServer();
    private SocketIOServer server;

    private SocketServer() {}

    public void initialize(int port) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(port);

        server = new SocketIOServer(config);
        server.addConnectListener((client) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Connected to socket");
        });
        server.addDisconnectListener(client -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - Disconnected from socket");
            // TODO: automatically remove user from game

            // TODO: check if game has any active user. Delete the game otherwise.
        });

        server.addEventListener("action", ActionObject.class, (client, data, ackSender) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - " + data.toString());

            Optional<String> roomId = client.getAllRooms().stream().filter((name) -> !name.equals("")).findFirst();
            if (roomId.isEmpty())
                throw new IllegalStateException("Socket ID ["+client.getSessionId().toString()+"] Room not found");

            Game game = GamesManager.getInstance().getGameInstance(roomId.get());

            ActionType requestedAction = ActionType.valueOf(data.getAction());
            ActionResult result = game.action(data.getxCoordinate(), data.getyCoordinate(), requestedAction);

            // broadcast messages to all clients
            String map = game.toString();
            switch (result) {
                case EXPLOSION:
                    server.getRoomOperations(roomId.get()).sendEvent("game_lost", new GameUpdateObject(map));
                    break;
                case GAME_OVER:
                    server.getRoomOperations(roomId.get()).sendEvent("game_won", new GameUpdateObject(map));
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
            Game game = GamesManager.getInstance().getGameInstance(data.getRoomName());
            String map = game.toString();
            client.sendEvent("game_update", new GameUpdateObject(map));

            // send new user connection to all connected users
//            int connectedClients = server.getRoomOperations(data.getRoomName()).getClients().size();
//            server.getRoomOperations(data.getRoomName()).sendEvent("connections_update",
//                    new NewConnectionObject(connectedClients));
        });
        server.addEventListener("leave_room", JoinRoomObject.class, (client, data, ackSender) -> {
            System.out.println("Socket ID ["+client.getSessionId().toString()+"] - " + data.toString());
            // add user to one game sessione
            client.leaveRoom(data.getRoomName());

            // send new user connection to all connected users
//            int connectedClients = server.getRoomOperations(data.getRoomName()).getClients().size();
//            server.getRoomOperations(data.getRoomName()).sendEvent("connections_update",
//                    new NewConnectionObject(connectedClients));
        });
        server.start();

        System.out.println("SocketIO server started on port " + port);
    }

    public void stopServer() {
        server.stop();
    }

    public static SocketServer getInstance() {
        return instance;
    }

}

