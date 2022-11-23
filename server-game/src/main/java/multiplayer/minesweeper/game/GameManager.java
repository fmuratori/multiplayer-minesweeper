package multiplayer.minesweeper.game;

import java.util.List;
import java.util.Optional;
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
    public synchronized void initialize(Optional<Long> seed) {
        this.grid = new TileContent[height][width];
        this.gridState = new TileState[height][width];

        // add mines at random positions inside the grid
        int num_mines = (int)((width / height) * difficulty.value);
        Random rand = new Random(seed.orElseGet(System::currentTimeMillis));
        IntStream
                .range(0,num_mines)
                .map(i -> rand.nextInt(width * height))
                .mapToObj(i -> new Coordinate(i / height, i % height))
                .forEach(point -> grid[point.x][point.y] = TileContent.MINE);

        this.precomputeGridContent();
    }

    /**
     * Initializes the game and provides the configuration of mines in a game.
     * This is a testing method.
     */
    public synchronized void initialize(List<Coordinate> mines_positions) {
        this.grid = new TileContent[height][width];
        this.gridState = new TileState[height][width];

        // add mines at random positions inside the grid
        mines_positions
                .forEach(point -> grid[point.x][point.y] = TileContent.MINE);

        this.precomputeGridContent();
    }

    private void precomputeGridContent() {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                gridState[i][j] = TileState.NOT_VISITED;
                // automatically generate the "final grid" and initialize a new grid for visited tiles
                AtomicInteger near_mines = new AtomicInteger();
                Stream.of(
                        new Coordinate(i-1, j-1),
                        new Coordinate(i-1, j),
                        new Coordinate(i-1, j+1),
                        new Coordinate(i, j-1),
                        new Coordinate(i, j+1),
                        new Coordinate(i+1, j-1),
                        new Coordinate(i+1, j),
                        new Coordinate(i+1, j+1)
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

    public synchronized ActionResult action(int x, int y, ActionType actionType) {
        if (gridState[x][y] != TileState.NOT_VISITED && gridState[x][y] != TileState.FLAGGED )
            throw new IllegalArgumentException("Tile (" + x + ", " + y + ") already visited");

        if (actionType == ActionType.VISIT) {

            // game lost
            if (grid[x][y] == TileContent.MINE) {
                gridState[x][y] = TileState.EXPLODED;
                return ActionResult.EXPLOSION;
            }

            // expand the visited area if an empty tile is selected
            this.visitAndExpand(x, y);

            // check for game over (all tiles are visited or flagged currectly)
            if (this.checkGameOver())
                return ActionResult.GAME_OVER;

        } else if (actionType == ActionType.FLAG) {
            gridState[x][y] = TileState.FLAGGED;
        }
        return ActionResult.OK;
    }

    private void visitAndExpand(int x, int y) {
        if (!(x >= 0 && x < this.width && y >= 0 && y < this.height))
            return;
        if (gridState[x][y] == TileState.VISITED)
            return;

        gridState[x][y] = TileState.VISITED;

        if (grid[x][y] == TileContent.EMPTY) {
            visitAndExpand(x+1, y);
            visitAndExpand(x-1, y);
            visitAndExpand(x, y+1);
            visitAndExpand(x, y-1);
            visitAndExpand(x+1, y+1);
            visitAndExpand(x+1, y-1);
            visitAndExpand(x-1, y+1);
            visitAndExpand(x-1, y-1);
        }
    }

    /**
     * Check if the game is ended. A game ends when every non-mine tile has been visited.
     * @return true if the game is over and the player has won, false if the game is not over and
     * tiles still need to be visited
     */
    public boolean checkGameOver() {
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                if (gridState[i][j] != TileState.VISITED && grid[i][j] != TileContent.MINE)
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns the current state of the grid as a matrix of integer values representing each tile type.
     */
    public synchronized String toString() {
        return "";
    }
}
