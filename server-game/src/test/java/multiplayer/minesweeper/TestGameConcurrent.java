package multiplayer.minesweeper;


import multiplayer.minesweeper.game.*;
import multiplayer.minesweeper.gameutils.GameModeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameConcurrent {
    private final int CONCURRENT_CHECKS = 10;
    private Game game = null;

    private final List<Pair<Integer, Integer>> minesList = List.of(
            new Pair<>(0, 0), new Pair<>(0, 2),
            new Pair<>(1, 1), new Pair<>(1, 3),
            new Pair<>(2, 0), new Pair<>(2, 2),
            new Pair<>(3, 1), new Pair<>(3, 3));
    private final List<Pair<Integer, Integer>> actions = new ArrayList<>(
            List.of(
            new Pair<>(0, 1), new Pair<>(0, 3),
            new Pair<>(1, 0), new Pair<>(1, 2),
            new Pair<>(2, 1), new Pair<>(2, 3),
            new Pair<>(3, 0), new Pair<>(3, 2)));

    /**
     * Grid layout:
     *  ╔═══╦═══╦═══╦═══╗
     *  ║ M ║   ║ M ║   ║
     *  ╠═══╬═══╬═══╬═══╣
     *  ║   ║ M ║   ║ M ║
     *  ╠═══╬═══╬═══╬═══╣
     *  ║ M ║   ║ M ║   ║
     *  ╠═══╬═══╬═══╬═══╣
     *  ║   ║ M ║   ║ M ║
     *  ╚═══╩═══╩═══╩═══╝
     */


    @BeforeEach
    void initialize() {
        int GRID_WIDTH = 4;
        int GRID_HEIGHT = 4;
        game = new Game(GameModeFactory.testGrid(GRID_WIDTH, GRID_HEIGHT));
        game.initialize(minesList);
    }

    @Test
    void testConcurrent() throws InterruptedException {
        for (int i = 0; i < CONCURRENT_CHECKS; i++) {
            Collections.shuffle(actions);
            List<Pair<Integer, Integer>> player1Actions = actions.subList(0, actions.size() / 2);
            List<Pair<Integer, Integer>> player2Actions = actions.subList(actions.size() / 2, actions.size() );

            Thread t1 = new Thread(() -> {
                player1Actions.forEach(p -> game.action(p.x, p.y, ActionType.VISIT));
            });
            Thread t2 = new Thread(() -> {
                player2Actions.forEach(p -> game.action(p.x, p.y, ActionType.VISIT));
            });
            t1.start();
            t2.start();

            t1.join();
            t2.join();

            assertTrue(game.isOver());
            assertFalse(game.isLost());
        }
    }
    @Test
    void testConcurrentWithOverlap() throws InterruptedException {
        for (int i = 0; i < CONCURRENT_CHECKS; i++) {
            Collections.shuffle(actions);
            Thread t1 = new Thread(() -> {
                actions.forEach(p -> game.action(p.x, p.y, ActionType.VISIT));
            });
            Thread t2 = new Thread(() -> {
                actions.forEach(p -> game.action(p.x, p.y, ActionType.VISIT));
            });
            t1.start();
            t2.start();

            t1.join();
            t2.join();

            assertTrue(game.isOver());
            assertFalse(game.isLost());
        }
    }
    @Test
    void testConcurrentWithGameOver() throws InterruptedException {
        for (int i = 0; i < CONCURRENT_CHECKS; i++) {
            // select a random mine position and add it to the actions that will be executed
            Random random = new Random();
            Pair<Integer, Integer> errorAction = minesList.get(random.nextInt(minesList.size()));
            actions.add(errorAction);

            // shuffle actions
            Collections.shuffle(actions);

            // simulate 2 players
            Thread t1 = new Thread(() -> {
                actions.forEach(p -> game.action(p.x, p.y, ActionType.VISIT));
            });
            Thread t2 = new Thread(() -> {
                actions.forEach(p -> game.action(p.x, p.y, ActionType.VISIT));
            });
            t1.start();
            t2.start();

            t1.join();
            t2.join();

            // remove error action
            actions.remove(errorAction);

            // check assertion
            assertTrue(game.isOver());
            assertTrue(game.isLost());
        }
    }
}
