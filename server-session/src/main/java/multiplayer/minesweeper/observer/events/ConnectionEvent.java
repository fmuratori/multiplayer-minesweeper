package multiplayer.minesweeper.observer.events;

public class ConnectionEvent {

    private final String clientId;
    private final String namespace;

    public ConnectionEvent(String namespace, String clientId) {
        this.namespace = namespace;
        this.clientId = clientId;
    }
}
