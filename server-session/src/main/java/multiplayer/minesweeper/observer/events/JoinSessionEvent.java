package multiplayer.minesweeper.observer.events;

import com.corundumstudio.socketio.SocketIOClient;

public class JoinSessionEvent {

    private final String roomName;

    private final int connectedClients;
    private final SocketIOClient client;

    public JoinSessionEvent(String roomName, int connectedClients, SocketIOClient client) {
        this.roomName = roomName;
        this.connectedClients = connectedClients;
        this.client = client;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getConnectedClients() {
        return connectedClients;
    }

    public SocketIOClient getClient() {
        return client;
    }
}
