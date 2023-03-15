package multiplayer.minesweeper;


import multiplayer.minesweeper.game.*;
import multiplayer.minesweeper.gameutils.GameModeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameCore {

    private Game gameManager = null;
    private final int GRID_WIDTH = 4;
    private final int GRID_HEIGHT = 4;

    @BeforeEach
    void initialize() {
        List<Pair<Integer, Integer>> minesList = List.of(
                new Pair<>(0, 0),
                new Pair<>(1, 1),
                new Pair<>(2, 0));
        gameManager = new Game(GameModeFactory.testGrid(GRID_WIDTH, GRID_HEIGHT));
        gameManager.initialize(minesList);
    }

    @Test
    void testInitialization() {
        assertSame(gameManager.getTiles()[0][0].getContent(), TileContent.MINE);
        assertSame(gameManager.getTiles()[1][1].getContent(), TileContent.MINE);
        assertSame(gameManager.getTiles()[2][0].getContent(), TileContent.MINE);
        assertSame(gameManager.getTiles()[0][1].getContent(), TileContent.NEAR_2);
        assertSame(gameManager.getTiles()[1][0].getContent(), TileContent.NEAR_3);
        assertSame(gameManager.getTiles()[2][2].getContent(), TileContent.NEAR_1);
        assertSame(gameManager.getTiles()[2][1].getContent(), TileContent.NEAR_2);

        for (int i  = 0; i < GRID_HEIGHT; i++)
            for (int j  = 0; j < GRID_WIDTH; j++)
                assertSame(gameManager.getTiles()[i][j].getState(), TileState.NOT_VISITED);
    }

    @Test
    public void testVisitAction() {
        ActionResult result = gameManager.action(1, 0, ActionType.VISIT);
        assertEquals(result, ActionResult.OK);

        int visitedTiles = 0;
        for (int i  = 0; i < GRID_HEIGHT; i++)
            for (int j  = 0; j < GRID_WIDTH; j++)
                if (gameManager.getTiles()[i][j].getState() == TileState.VISITED) visitedTiles++;

        assertEquals(visitedTiles, 1);

    }
    @Test
    public void testVisitActionWithExpansion() {
        ActionResult result = gameManager.action(3, 3, ActionType.VISIT);
        assertEquals(result, ActionResult.OK);

        int visitedTiles = 0;
        for (int i  = 0; i < GRID_HEIGHT; i++)
            for (int j  = 0; j < GRID_WIDTH; j++)
                if (gameManager.getTiles()[i][j].getState() == TileState.VISITED) visitedTiles++;

        assertEquals(visitedTiles, 10); // 4*4 tiles - (3 mines + 1 isolated tile)
    }

    @Test
    public void testFlagAction() {
        ActionResult result = gameManager.action(0, 0, ActionType.FLAG);
        assertEquals(result, ActionResult.OK);
        assertEquals(gameManager.getTiles()[0][0].getState(), TileState.FLAGGED);

        ActionResult result2 = gameManager.action(0, 0, ActionType.FLAG);
        assertEquals(result2, ActionResult.OK);
        assertEquals(gameManager.getTiles()[0][0].getState(), TileState.NOT_VISITED);

        ActionResult result3 = gameManager.action(0, 1, ActionType.FLAG);
        assertEquals(result3, ActionResult.OK);
        assertEquals(gameManager.getTiles()[0][1].getState(), TileState.FLAGGED);
    }

    @Test
    public void testActionWithExplosion() {
        ActionResult result1 = gameManager.action(1, 0, ActionType.VISIT);
        assertEquals(result1, ActionResult.OK);

        ActionResult result2 = gameManager.action(1, 1, ActionType.VISIT);
        assertEquals(result2, ActionResult.EXPLOSION);

        assertEquals(gameManager.getTiles()[1][1].getState(), TileState.EXPLODED);

        ActionResult result3 = gameManager.action(1, 1, ActionType.VISIT);
        assertEquals(result3, ActionResult.IGNORED);
    }

    @Test
    public void testFirstActionWithExplosion() {
        ActionResult result1 = gameManager.action(0, 0, ActionType.VISIT);
        assertEquals(result1, ActionResult.OK);

        assertEquals(gameManager.getTiles()[0][0].getState(), TileState.VISITED);
    }

    @Test
    public void testGameOver() {
        ActionResult result1 = gameManager.action(3, 3, ActionType.VISIT);
        ActionResult result2 = gameManager.action(3, 0, ActionType.VISIT);
        ActionResult result3 = gameManager.action(0, 1, ActionType.VISIT);
        ActionResult result4 = gameManager.action(1, 0, ActionType.VISIT);
        assertEquals(result1, ActionResult.OK);
        assertEquals(result2, ActionResult.OK);
        assertEquals(result3, ActionResult.OK);
        assertEquals(result4, ActionResult.GAME_OVER);
    }
}
