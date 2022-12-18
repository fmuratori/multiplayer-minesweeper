package multiplayer.minesweeper.socket.out;

public enum SessionUpdateType {
    ADDED_USER("ADDED_USER"),
    REMOVED_USER("REMOVED_USER"),
    CLOSED("REMOVED_USER"),
    GAME_STARTING("REMOVED_USER");

    private final String value;

    SessionUpdateType(String value) {this.value = value;};
    public String getValue() {
        return value;
    }

}
