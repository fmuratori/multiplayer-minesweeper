package multiplayer.minesweeper.game;

public enum TileState {
    VISITED("VISITED"), NOT_VISITED("NOT_VISITED"), FLAGGED("FLAGGED"), EXPLODED("EXPLODED");

    public final String value;

    private TileState(String value) {
        this.value = value;
    }
}
