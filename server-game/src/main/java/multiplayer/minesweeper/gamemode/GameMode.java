package multiplayer.minesweeper.gamemode;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameMode {

    private final String name;
    private final int gridWidth;
    private final int gridHeight;
    private final int numPlayers;

    private final float numMines;

    GameMode(String name, int gridWidth, int gridHeight, int numPlayers, int numMines) {
        this.name = name;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.numPlayers = numPlayers;
        this.numMines = numMines;
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

    public float getNumMines() {
        return numMines;
    }
}
