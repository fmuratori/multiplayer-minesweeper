package multiplayer.minesweeper.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamesManager {
    private static final GamesManager instance = new GamesManager();
    private final Map<String, Game> activeGames = new HashMap<>();

    private GamesManager() {}

    public static GamesManager getInstance() {
        return instance;
    }

    public String newGame(int gridWidth, int gridHeight, float minesPercentage) {
        String gameId = UUID.randomUUID().toString();
        Game newInstance = new Game(gridWidth, gridHeight, minesPercentage);
        newInstance.initialize();
        activeGames.put(gameId, newInstance);
        return gameId;
    }

    public String testGame() {
        String gameId = "test_room";
        Game newInstance = new Game(30, 16, 0.2f);
        newInstance.initialize();
        activeGames.put(gameId, newInstance);
        return gameId;
    }

    public Game getGameInstance(String roomId) {
        if (!activeGames.containsKey(roomId))
            throw new IllegalArgumentException();
        return activeGames.get(roomId);
    }
}
