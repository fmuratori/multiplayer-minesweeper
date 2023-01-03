package multiplayer.minesweeper.game;

public enum TileContent {
    MINE("M"), EMPTY("C"),
    NEAR_1("1"), NEAR_2("2"), NEAR_3("3"), NEAR_4("4"),
    NEAR_5("5"), NEAR_6("6"), NEAR_7("7"), NEAR_8("8");

    /**
     * M: MINE
     * C: CLEAR
     */
    public final String value;

    TileContent(String value) {
        this.value = value;
    }
}
