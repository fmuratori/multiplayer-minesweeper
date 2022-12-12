package multiplayer.minesweeper.game;

public enum TileContent {
    MINE("MINE"), EMPTY("EMPTY"),
    NEAR_1("NEAR_1"), NEAR_2("NEAR_2"), NEAR_3("NEAR_3"), NEAR_4("NEAR_4"),
    NEAR_5("NEAR_5"), NEAR_6("NEAR_6"), NEAR_7("NEAR_7"), NEAR_8("NEAR_8");

    public final String value;

    private TileContent(String value) {
        this.value = value;
    }
}
