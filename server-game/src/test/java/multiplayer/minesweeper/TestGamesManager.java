package multiplayer.minesweeper;


import multiplayer.minesweeper.game.*;
import multiplayer.minesweeper.game.gamemode.GameModeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestGamesManager {

    private GamesManager gamesManager;

    @BeforeEach
    void initialize() {
        gamesManager = new GamesManager();
    }


    @Test
    void testGameCreation() {
        gamesManager.newGame(GameModeFactory.smallGrid());
        assertEquals(gamesManager.getActiveGames().size(), 1);

        gamesManager.newGame(GameModeFactory.smallGrid());
        assertEquals(gamesManager.getActiveGames().size(), 2);
    }

    @Test
    void testGameDeletion() {
        String gameId = gamesManager.newGame(GameModeFactory.smallGrid());

        gamesManager.deleteGame(gameId);

        assertEquals(gamesManager.getActiveGames().size(), 0);
    }


    @Test
    void testGamePlayersCreation() {
        String gameId = gamesManager.newGame(GameModeFactory.smallGrid());

        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();

        Game game = gamesManager.getGameInstance(gameId);

        game.addPlayer(player1);
        assertTrue(game.containsPlayer(player1));

        assertFalse(game.containsPlayer(player2));
        game.addPlayer(player2);
        assertTrue(game.containsPlayer(player2));
    }

    @Test
    void testGamePlayersDeletion() {
        String gameId = gamesManager.newGame(GameModeFactory.smallGrid());

        UUID player1 = UUID.randomUUID();

        Game game = gamesManager.getGameInstance(gameId);

        assertFalse(game.containsPlayer(player1));
        game.removePlayer(player1);
        assertFalse(game.containsPlayer(player1));

        game.addPlayer(player1);
        assertTrue(game.containsPlayer(player1));

        game.removePlayer(player1);
        assertFalse(game.containsPlayer(player1));
    }
}
