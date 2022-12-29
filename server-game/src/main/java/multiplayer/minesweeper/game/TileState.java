package multiplayer.minesweeper.game;

public enum TileState {
    VISITED("V"), NOT_VISITED("N"), FLAGGED("F"), EXPLODED("E");
    /**
     * V: VISITED
     * N: NOT_VISITED
     * F: FLAGGED
     * E: EXPLOSION
     */

    public final String value;

    private TileState(String value) {
        this.value = value;
    }
}
