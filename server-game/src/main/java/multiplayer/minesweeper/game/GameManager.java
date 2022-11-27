package multiplayer.minesweeper.game;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameManager {

    private final int width;
    private final int height;

    private TileContent[][] grid;
    private TileState[][] gridState;

    private boolean gameOverFlag = false;

    public GameManager(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Initializes the game, creates and defines the positions of mines in the grid
     */
    public synchronized void initialize(Optional<Long> seed, GameDifficulty difficulty) {
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

        // fill matrices with default values
        for (TileContent[] row : grid)
            Arrays.fill(row, TileContent.EMPTY);
        for (TileState[] row : gridState)
            Arrays.fill(row, TileState.NOT_VISITED);

        // add mines at random positions inside the grid
        mines_positions
                .forEach(point -> grid[point.x][point.y] = TileContent.MINE);

        this.precomputeGridContent();
    }

    private void precomputeGridContent() {
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if (grid[i][j] == TileContent.MINE)
                    continue;

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
                    if (c.x >= 0 && c.x < height && c.y >= 0 && c.y < width) {
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

    /**
     * Execute an action on the game grid at a given coordinate.
     *
     * @param x the row coordinate
     * @param y the column coordinate
     * @param actionType the action to be executed
     *
     * @return the result of an action.
     */
    public synchronized ActionResult action(int x, int y, ActionType actionType) {

        if (gameOverFlag || (gridState[x][y] != TileState.NOT_VISITED && gridState[x][y] != TileState.FLAGGED))
            return ActionResult.IGNORED;

        switch (actionType) {
            case UNFLAG:
                gridState[x][y] = TileState.NOT_VISITED;
                return ActionResult.OK;
            case VISIT:
                // game lost
                if (grid[x][y] == TileContent.MINE) {
                    gameOverFlag = true;
                    gridState[x][y] = TileState.EXPLODED;
                    return ActionResult.EXPLOSION;
                }

                // expand the visited area if an empty tile is selected
                this.visitAndExpand(x, y);

                // check for game over (all tiles are visited or flagged correctly)
                if (this.checkGameOver()) {
                    gameOverFlag = true;
                    return ActionResult.GAME_OVER;
                }
                return ActionResult.OK;
            case FLAG:
                gridState[x][y] = TileState.FLAGGED;
                return ActionResult.OK;
            default:
                return ActionResult.IGNORED;
        }
    }

    private void visitAndExpand(int x, int y) {
        if (!(x >= 0 && x < this.width && y >= 0 && y < this.height))
            return;

        // TODO: valutare se rimuovere seguente if
        if (gridState[x][y] == TileState.VISITED)
            return;

        if (grid[x][y] != TileContent.MINE) {

            gridState[x][y] = TileState.VISITED;
            visitAndExpand(x+1, y);
            visitAndExpand(x-1, y);
            visitAndExpand(x, y+1);
            visitAndExpand(x, y-1);
//            visitAndExpand(x+1, y+1);
//            visitAndExpand(x+1, y-1);
//            visitAndExpand(x-1, y+1);
//            visitAndExpand(x-1, y-1);
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

    public TileContent[][] getGrid() {
        return grid;
    }

    public TileState[][] getGridState() {
        return gridState;
    }
}
