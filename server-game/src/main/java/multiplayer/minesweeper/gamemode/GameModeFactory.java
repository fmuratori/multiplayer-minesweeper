package multiplayer.minesweeper.gamemode;

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

    public static List<GameMode> getAllGameModes() {
        return Arrays.asList(smallGrid(), mediumGrid(), bigGrid());
    }

}
