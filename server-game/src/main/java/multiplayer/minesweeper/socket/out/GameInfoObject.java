package multiplayer.minesweeper.socket.out;

import multiplayer.minesweeper.gamemode.GameMode;

public class GameInfoObject extends GameUpdateObject {

    private int minesCount;
    private int numMaxPlayers;
    private int gridWidth;
    private int gridHeight;

    private String startedAt;

    public GameInfoObject() {}

    public GameInfoObject(String map, GameMode gameMode, String startedAt) {
        super(map);
        this.minesCount = gameMode.getNumMines();
        this.numMaxPlayers = gameMode.getNumPlayers();
        this.gridWidth = gameMode.getGridWidth();
        this.gridHeight = gameMode.getGridHeight();
        this.startedAt = startedAt;
    }

    public int getMinesCount() {
        return minesCount;
    }

    public int getNumMaxPlayers() {
        return numMaxPlayers;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public String getStartedAt() {
        return startedAt;
    }
}
