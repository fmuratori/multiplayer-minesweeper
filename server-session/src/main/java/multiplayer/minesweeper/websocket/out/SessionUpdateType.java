package multiplayer.minesweeper.websocket.out;

public enum SessionUpdateType {
    ADDED_USER("ADDED_USER"),
    REMOVED_USER("REMOVED_USER"),
    NEW_SESSION("NEW_SESSION"),
    GAME_STARTING("GAME_STARTING");

    private final String value;

    SessionUpdateType(String value) {this.value = value;};
    public String getValue() {
        return value;
    }

}
