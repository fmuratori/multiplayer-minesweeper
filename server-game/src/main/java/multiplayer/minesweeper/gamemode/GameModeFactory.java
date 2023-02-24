package multiplayer.minesweeper.gamemode;

import multiplayer.minesweeper.game.Coordinate;

import java.util.Arrays;
import java.util.List;

public class GameModeFactory {
    public static GameMode smallGrid() {
        return new GameMode("SMALL", 9, 9, 1, 10);
    }

    public static GameMode mediumGrid() {
        return new GameMode("MEDIUM", 16, 16, 2, 40);
    }

    public static GameMode bigGrid() {
        return new GameMode("BIG", 30, 16, 2, 90);
    }

    public static GameMode testGrid(int width, int height) {
        return new GameMode("TEST", width, height, 0, 0);
    }

    public static List<GameMode> getAllGameModes() {
        return Arrays.asList(smallGrid(), mediumGrid(), bigGrid());
    }

    public static GameMode getByName(String name) {
        switch (name) {
            case "MEDIUM": return mediumGrid();
            case "BIG": return bigGrid();
            case "SMALL": return smallGrid();
            default: throw new IllegalArgumentException();
        }
    }

}
