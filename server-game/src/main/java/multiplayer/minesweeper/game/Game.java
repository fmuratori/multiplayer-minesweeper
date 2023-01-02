package multiplayer.minesweeper.game;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Game {
    private float minesPercentage = 0.4f;
    private final int width;
    private final int height;
    private int visitedCount = 0;
    private int toVisitCount = 0;

    private TileContent[][] grid;
    private TileState[][] gridState;

    private boolean gameOverFlag = false;
    private boolean firstActionFlag = true;

    private final Set<UUID> connectedPlayers = new HashSet<>();

    public Game(int width, int height, float minesPercentage) {
        this.width = width;
        this.height = height;
        this.minesPercentage = minesPercentage;
    }

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Initializes the game, creates and defines the positions of mines in the grid
     */
    public synchronized void initialize() {
        initializeGrids();

        // add mines at random positions inside the grid
        int num_mines = (int)((width * height) * minesPercentage);
        Random rand = new Random(System.currentTimeMillis());
        IntStream
                .range(0,num_mines)
                .map(i -> rand.nextInt(width * height))
                .mapToObj(i -> new Coordinate(i / width, i % width))
                .forEach(point -> grid[point.x][point.y] = TileContent.MINE);

        precomputeGridContent();
    }

    /**
     * Initializes the game and provides the configuration of mines in a game.
     * This is a testing method.
     */
    public synchronized void initialize(List<Coordinate> mines_positions) {
        initializeGrids();

        // add mines at random positions inside the grid
        mines_positions
                .forEach(point -> grid[point.x][point.y] = TileContent.MINE);

        precomputeGridContent();

        firstActionFlag = true;
    }

    private void initializeGrids() {
        this.grid = new TileContent[height][width];
        this.gridState = new TileState[height][width];

        // fill matrices with default values
        for (TileContent[] row : grid)
            Arrays.fill(row, TileContent.EMPTY);
        for (TileState[] row : gridState)
            Arrays.fill(row, TileState.NOT_VISITED);
    }

    private void precomputeGridContent() {
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if (grid[i][j] == TileContent.MINE)
                    continue;

                // automatically generate the "final grid" and initialize a new grid for visited tiles
                int nearMinesCount = computeNearMinesCount(i, j);
                switch (nearMinesCount) {
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

        visitedCount = 0;

        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++)
                if (grid[i][j] != TileContent.MINE)
                    toVisitCount++;
    }

    private int computeNearMinesCount(int i, int j) {
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

        return near_mines.get();
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
            case VISIT:

                // always allow the first action
                if (firstActionFlag) {
                    if (grid[x][y] == TileContent.MINE) {
                        grid[x][y] = TileContent.EMPTY;
                        precomputeGridContent();
                    }
                    firstActionFlag = false;
                }

                // game lost
                if (grid[x][y] == TileContent.MINE) {
                    gameOverFlag = true;
                    gridState[x][y] = TileState.EXPLODED;
                    return ActionResult.EXPLOSION;
                }

                // expand the visited area if an empty tile is selected
                this.visitAndExpand(x, y);

                // check for game over (all tiles are visited or flagged correctly)
                if (this.toVisitCount == this.visitedCount) {
                    gameOverFlag = true;
                    return ActionResult.GAME_OVER;
                }
                return ActionResult.OK;
            case FLAG:
                if (gridState[x][y] == TileState.FLAGGED)
                    gridState[x][y] = TileState.NOT_VISITED;
                else if (gridState[x][y] == TileState.NOT_VISITED)
                    gridState[x][y] = TileState.FLAGGED;
                return ActionResult.OK;
            default:
                return ActionResult.IGNORED;
        }
    }

    private void visitAndExpand(int x, int y) {
        if (!(x >= 0 && x < this.height && y >= 0 && y < this.width))
            return;

        if (gridState[x][y] == TileState.VISITED)
            return;

        gridState[x][y] = TileState.VISITED;
        visitedCount++;
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
     * Returns the current state of the grid as a matrix of integer values representing each tile type.
     */
    public synchronized String toString() {
        String[] output = new String[height*width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                String value;
                if (gridState[i][j] == TileState.VISITED || (gameOverFlag && gridState[i][j] != TileState.EXPLODED)) {
                    value = grid[i][j].value;
                } else {
                    value = gridState[i][j].value;
                }
                output[ i*width + j] = value;
            }
        }

        return String.join(" ", output);
    }

    public TileContent[][] getGrid() {
        return grid;
    }

    public TileState[][] getGridState() {
        return gridState;
    }

    public void addPlayer(UUID newPlayerId) {
        connectedPlayers.add(newPlayerId);
    }

    public boolean containsPlayer(UUID playerId) {
        return connectedPlayers.contains(playerId);
    }

    public void removePlayer(UUID playerId) {
        connectedPlayers.remove(playerId);
    }
}
