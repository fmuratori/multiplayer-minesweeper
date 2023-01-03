package multiplayer.minesweeper;


import multiplayer.minesweeper.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestGame {

    private Game gameManager = null;
    private final int GRID_WIDTH = 4;
    private final int GRID_HEIGHT = 4;

    @BeforeEach
    void initialize() {
        List<Coordinate> minesList = List.of(
                new Coordinate(0, 0),
                new Coordinate(1, 1),
                new Coordinate(2, 0));
        gameManager = new Game(GRID_WIDTH, GRID_HEIGHT);
        gameManager.initialize(minesList);
    }

    @Test
    void testInitialization() {
        assertSame(gameManager.getGrid()[0][0], TileContent.MINE);
        assertSame(gameManager.getGrid()[1][1], TileContent.MINE);
        assertSame(gameManager.getGrid()[2][0], TileContent.MINE);
        assertSame(gameManager.getGrid()[0][1], TileContent.NEAR_2);
        assertSame(gameManager.getGrid()[1][0], TileContent.NEAR_3);
        assertSame(gameManager.getGrid()[2][2], TileContent.NEAR_1);
        assertSame(gameManager.getGrid()[2][1], TileContent.NEAR_2);

        for (int i  = 0; i < GRID_HEIGHT; i++)
            for (int j  = 0; j < GRID_WIDTH; j++)
                assertSame(gameManager.getGridState()[i][j], TileState.NOT_VISITED);
    }

    @Test
    public void testVisitAction() {
        ActionResult result = gameManager.action(1, 0, ActionType.VISIT);
        assertEquals(result, ActionResult.OK);

        int visitedTiles = 0;
        for (int i  = 0; i < GRID_HEIGHT; i++)
            for (int j  = 0; j < GRID_WIDTH; j++)
                if (gameManager.getGridState()[i][j] == TileState.VISITED) visitedTiles++;

        assertEquals(visitedTiles, 1);

    }
    @Test
    public void testVisitActionWithExpansion() {
        ActionResult result = gameManager.action(3, 3, ActionType.VISIT);
        assertEquals(result, ActionResult.OK);

        int visitedTiles = 0;
        for (int i  = 0; i < GRID_HEIGHT; i++)
            for (int j  = 0; j < GRID_WIDTH; j++)
                if (gameManager.getGridState()[i][j] == TileState.VISITED) visitedTiles++;

        assertEquals(visitedTiles, 10); // 4*4 tiles - (3 mines + 1 isolated tile)
    }

    @Test
    public void testFlagAction() {
        ActionResult result = gameManager.action(0, 0, ActionType.FLAG);
        assertEquals(result, ActionResult.OK);
        assertEquals(gameManager.getGridState()[0][0], TileState.FLAGGED);

        ActionResult result2 = gameManager.action(0, 0, ActionType.FLAG);
        assertEquals(result2, ActionResult.OK);
        assertEquals(gameManager.getGridState()[0][0], TileState.NOT_VISITED);

        ActionResult result3 = gameManager.action(0, 1, ActionType.FLAG);
        assertEquals(result3, ActionResult.OK);
        assertEquals(gameManager.getGridState()[0][1], TileState.FLAGGED);
    }

    @Test
    public void testActionWithExplosion() {
        ActionResult result1 = gameManager.action(1, 0, ActionType.VISIT);
        assertEquals(result1, ActionResult.OK);

        ActionResult result2 = gameManager.action(1, 1, ActionType.VISIT);
        assertEquals(result2, ActionResult.EXPLOSION);

        assertEquals(gameManager.getGridState()[1][1], TileState.EXPLODED);

        ActionResult result3 = gameManager.action(1, 1, ActionType.VISIT);
        assertEquals(result3, ActionResult.IGNORED);
    }

    @Test
    public void testFirstActionWithExplosion() {
        ActionResult result1 = gameManager.action(0, 0, ActionType.VISIT);
        assertEquals(result1, ActionResult.OK);

        assertEquals(gameManager.getGridState()[0][0], TileState.VISITED);
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
