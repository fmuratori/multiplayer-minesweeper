package multiplayer.minesweeper.sessions;

public enum GameMode {
    SMALL_GRID("SMALL", 9, 9, 1, 0.081f),
    MEDIUM_GRID("MEDIUM", 16, 16, 2, 0.15625f),
    BIG_GRID("BIG", 30, 16, 4, 0.20625f);

    private final String name;
    private final int gridWidth;
    private final int gridHeight;
    private final int numPlayers;
    private final float minesPercentage;

    GameMode(String name, int gridWidth, int gridHeight, int numPlayers, float minesPercentage) {
        this.name = name;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.numPlayers = numPlayers;
        this.minesPercentage = minesPercentage;
    }

    public String getName() {
        return name;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public float getMinesPercentage() { return minesPercentage; }

    public static GameMode getEnum(String name) {
        switch (name) {
            case "SMALL": return GameMode.SMALL_GRID;
            case "MEDIUM": return GameMode.MEDIUM_GRID;
            case "BIG": return GameMode.BIG_GRID;
            default: throw new IllegalArgumentException("Value " + name + "not found");
        }
    }
}
