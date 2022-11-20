package multiplayer.minesweeper.game;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameManager {

    private final int width;
    private final int height;
    private final GameDifficulty difficulty;
    private TileContent[][] grid;
    private TileState[][] gridState;

    public GameManager(int width, int height, GameDifficulty difficulty) {
        this.width = width;
        this.height = height;
        this.difficulty = difficulty;
    }

    /**
     * Initializes the game, creates and defines the positions of mines in the grid
     */
    public synchronized void initialize() {
        this.grid = new TileContent[height][width];
        this.gridState = new TileState[height][width];

        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                gridState[i][j] = TileState.NOT_VISITED;

                // add mines at random positions inside the grid
                int num_mines = (int)((width / height) * difficulty.value);
                Random rand = new Random(System.currentTimeMillis());
                IntStream
                        .range(0,num_mines)
                        .map(i -> rand.nextInt(width * height))
                        .mapToObj(i -> new Point(i / height, i % height))
                        .forEach(point -> grid[point.x][point.y] = TileContent.MINE);

                // automatically generate the "final grid" and initialize a new grid for visited tiles
                AtomicInteger near_mines = new AtomicInteger();
                Stream.of(
                    new Point(i-1, j-1),
                    new Point(i-1, j),
                    new Point(i-1, j+1),
                    new Point(i, j-1),
                    new Point(i, j+1),
                    new Point(i+1, j-1),
                    new Point(i+1, j),
                    new Point(i+1, j+1)
                ).parallel().forEach(c -> {
                    if (c.x >= 0 && c.y < width && c.y >= 0 && c.y < height) {
                        if (grid[c.x][c.y] == TileContent.MINE)
                            near_mines.getAndIncrement();
                    }
                });
                switch (near_mines.get()) {
                    case 0: grid[i][j] = TileContent.EMPTY; break;
                    case 1: grid[i][j] = TileContent.NEAR_1; break;
                    case 2: grid[i][j] = TileContent.NEAR_2; break;
                    case 3: grid[i][j] = TileContent.NEAR_3; break;
                    case 4: grid[i][j] = TileContent.NEAR_4; break;
                    case 5: grid[i][j] = TileContent.NEAR_5; break;
                    case 6: grid[i][j] = TileContent.NEAR_6; break;
                    case 7: grid[i][j] = TileContent.NEAR_7; break;
                    case 8: grid[i][j] = TileContent.NEAR_8; break;
                }
            }
        }
    }

    public synchronized ActionResult action(int x, int y) {
        if (gridState[x][y] != TileState.NOT_VISITED)
            throw new IllegalArgumentException("Tile (" + x + ", " + y + ") already visited");

        gridState[x][y] = TileState.VISITED;

        // game lost
        if (grid[x][y] == TileContent.MINE)
            return ActionResult.EXPLOSION;

        // expand the visited area if an empty tile is selected

        // check for game over (all tiles are visited or flagged currectly)


        return ActionResult.OK;
    }

    /**
     * Returns the current state of the grid as a matrix of integer values representing each tile type.
     */
    public synchronized String toString() {
        return "";
    }
}
