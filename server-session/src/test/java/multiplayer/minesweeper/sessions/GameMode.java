package multiplayer.minesweeper.sessions;

public enum GameMode {
    SMALL_GRID("SMALL", 8, 8, 1),
    MEDIUM_GRID("MEDIUM", 16, 16, 2),
    BIG_GRID("BIG", 40, 40, 4);

    private final String name;
    private final int gridWidth;
    private final int gridHeight;
    private final int numPlayers;

    GameMode(String name, int gridWidth, int gridHeight, int numPlayers) {
        this.name = name;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.numPlayers = numPlayers;
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

    public static GameMode getEnum(String name) {
        switch (name) {
            case "SMALL": return GameMode.SMALL_GRID;
            case "MEDIUM": return GameMode.MEDIUM_GRID;
            case "BIG": return GameMode.BIG_GRID;
            default: throw new IllegalArgumentException("Value " + name + "not found");
        }
    }
}
