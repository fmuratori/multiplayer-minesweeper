package multiplayer.minesweeper.observer.events;

public class DisconnectionEvent {

    private final String clientId;
    private final String namespace;

    public DisconnectionEvent(String namespace, String clientId) {
        this.namespace = namespace;
        this.clientId = clientId;
    }
}
