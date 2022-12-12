package multiplayer.minesweeper.game;

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

    public String newGame(int gridWidth, int gridHeight) {
//        String gameId = UUID.randomUUID().toString();
        String gameId = "test_room";
        Game newInstance = new Game(gridWidth, gridHeight);

        activeGames.put(gameId, newInstance);

        return gameId;
    }

    public Game getGameInstance(String roomId) {
        if (!activeGames.containsKey(roomId))
            throw new IllegalArgumentException();
        return activeGames.get(roomId);
    }
}