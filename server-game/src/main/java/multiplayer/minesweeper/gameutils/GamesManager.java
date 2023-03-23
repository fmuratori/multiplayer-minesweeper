package multiplayer.minesweeper.gameutils;

import multiplayer.minesweeper.game.Game;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GamesManager {
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    public GamesManager() {
    }

    public synchronized String newGame(GameMode mode) {
        String gameId = UUID.randomUUID().toString();
        Game newInstance = new Game(mode);
        newInstance.initialize();
        activeGames.put(gameId, newInstance);
        return gameId;
    }

    public synchronized void deleteGame(String roomId) {
        activeGames.remove(roomId);
    }

    public Optional<Game> getGameInstance(String roomId) {
        if (activeGames.containsKey(roomId))
            return Optional.of(activeGames.get(roomId));
        else
            return Optional.empty();

    }

    public synchronized Optional<String> findGameByUser(UUID playerId) {
        for (Map.Entry<String, Game> elem : activeGames.entrySet()) {
            if (elem.getValue().containsPlayer(playerId)) {
                return Optional.of(elem.getKey());
            }
        }
        return Optional.empty();
    }

    public synchronized Map<String, Game> getActiveGames() {
        return activeGames;
    }
}
