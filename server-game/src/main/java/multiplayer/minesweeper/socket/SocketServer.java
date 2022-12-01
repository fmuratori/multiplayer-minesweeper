package multiplayer.minesweeper.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import multiplayer.minesweeper.game.ActionResult;
import multiplayer.minesweeper.game.ActionType;
import multiplayer.minesweeper.game.Game;
import multiplayer.minesweeper.game.GamesManager;

public class SocketServer {

    private static final SocketServer instance = new SocketServer();
    private SocketIOServer server;

    private SocketServer() {}

    public void initialize() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9000);

        server = new com.corundumstudio.socketio.SocketIOServer(config);
        server.start();
        System.out.println("SocketIO server started on port " + 9000);
    }

    public void registerNewRoom(String roomId) {

        final SocketIONamespace newRoom = server.addNamespace("/" + roomId);

        newRoom.addEventListener("action", ActionObject.class, (client, data, ackRequest) -> {

            Game game = GamesManager.getInstance().getGameInstance(roomId);

            ActionType requestedAction = ActionType.valueOf(data.getAction());

            ActionResult result = game.action(data.getxCoordinate(), data.getyCoordinate(), requestedAction);

            // broadcast messages to all clients
            newRoom.getBroadcastOperations().sendEvent("UPDATE", result.toString());
        });
    }

    public void stopServer() {
        server.stop();
    }

    public static SocketServer getInstance() {
        return instance;
    }

}

