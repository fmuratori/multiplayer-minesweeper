package multiplayer.minesweeper.game;

import multiplayer.minesweeper.gamemode.GameMode;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GamesManager {
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    public GamesManager() {
    }

    public String newGame(GameMode mode) {
        String gameId = UUID.randomUUID().toString();
        Game newInstance = new Game(mode);
        newInstance.initialize();
        activeGames.put(gameId, newInstance);
        return gameId;
    }

    public void deleteGame(String roomId) {
        activeGames.remove(roomId);
    }

    public Game getGameInstance(String roomId) {
        if (!activeGames.containsKey(roomId))
            throw new IllegalArgumentException();
        return activeGames.get(roomId);
    }

    public Optional<String> findGameByUser(UUID playerId) {
        for (Map.Entry<String, Game> elem : activeGames.entrySet()) {
            if (elem.getValue().containsPlayer(playerId)) {
                return Optional.of(elem.getKey());
            }
        }
        return Optional.empty();
    }

    public Map<String, Game> getActiveGames() {
        return activeGames;
    }
}
