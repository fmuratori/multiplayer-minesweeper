package multiplayer.minesweeper.game;

import multiplayer.minesweeper.gameutils.GameMode;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Game {
    private final GameMode gameMode;
    private final Instant startedAt;
    private final Set<UUID> connectedPlayers = new HashSet<>();
    private Duration duration;
    private int visitedCount = 0;
    private int toVisitCount = 0;
    private Tile[][] tiles;
    private boolean gameOverFlag = false;
    private boolean gameLostFlag = false;
    private boolean firstActionFlag = true;

    public Game(GameMode mode) {
        gameMode = mode;
        startedAt = Instant.now();
    }

    /**
     * Initializes the game, creates and defines the positions of mines in the grid
     */
    public synchronized void initialize() {
        initializeGrids();

        // add mines at random positions inside the grid
        Random rand = new Random(System.currentTimeMillis());
        List<Pair<Integer, Integer>> minesPositions = IntStream
                .range(0, gameMode.getNumMines())
                .map(i -> rand.nextInt(gameMode.getGridWidth() * gameMode.getGridHeight()))
                .mapToObj(i -> new Pair<>(i / gameMode.getGridWidth(), i % gameMode.getGridWidth()))
                .collect(Collectors.toList());

        minesPositions
                .forEach(point -> tiles[point.x][point.y].setContent(TileContent.MINE));

        precomputeGridContent();
    }

    /**
     * Initializes the game and provides the configuration of mines in a game.
     * This is a testing method.
     */
    public synchronized void initialize(List<Pair<Integer, Integer>> minesPositions) {
        initializeGrids();

        // add mines at random positions inside the grid
        minesPositions
                .forEach(point -> tiles[point.x][point.y].setContent(TileContent.MINE));

        precomputeGridContent();

        firstActionFlag = true;
    }

    private void initializeGrids() {
        tiles = new Tile[gameMode.getGridHeight()][gameMode.getGridWidth()];
        for (int i = 0; i < gameMode.getGridHeight(); i++) {
            for (int j = 0; j < gameMode.getGridWidth(); j++) {
                tiles[i][j] = new Tile(TileContent.EMPTY, TileState.NOT_VISITED);
            }
        }
    }

    private void precomputeGridContent() {
        for (int i = 0; i < gameMode.getGridHeight(); i++) {
            for (int j = 0; j < gameMode.getGridWidth(); j++) {
                if (tiles[i][j].getContent() == TileContent.MINE)
                    continue;

                // automatically generate the "final grid" and initialize a new grid for visited tiles
                int nearMinesCount = computeNearMinesCount(i, j);

                switch (nearMinesCount) {
                    case 0:
                        tiles[i][j].setContent(TileContent.EMPTY);
                        break;
                    case 1:
                        tiles[i][j].setContent(TileContent.NEAR_1);
                        break;
                    case 2:
                        tiles[i][j].setContent(TileContent.NEAR_2);
                        break;
                    case 3:
                        tiles[i][j].setContent(TileContent.NEAR_3);
                        break;
                    case 4:
                        tiles[i][j].setContent(TileContent.NEAR_4);
                        break;
                    case 5:
                        tiles[i][j].setContent(TileContent.NEAR_5);
                        break;
                    case 6:
                        tiles[i][j].setContent(TileContent.NEAR_6);
                        break;
                    case 7:
                        tiles[i][j].setContent(TileContent.NEAR_7);
                        break;
                    case 8:
                        tiles[i][j].setContent(TileContent.NEAR_8);
                        break;
                }
            }
        }

        visitedCount = 0;
        for (int i = 0; i < gameMode.getGridHeight(); i++)
            for (int j = 0; j < gameMode.getGridWidth(); j++)
                if (tiles[i][j].getContent() != TileContent.MINE)
                    toVisitCount++;
    }

    private int computeNearMinesCount(int i, int j) {
        AtomicInteger near_mines = new AtomicInteger();
        Stream.of(
                new Pair<>(i - 1, j - 1),
                new Pair<>(i - 1, j),
                new Pair<>(i - 1, j + 1),
                new Pair<>(i, j - 1),
                new Pair<>(i, j + 1),
                new Pair<>(i + 1, j - 1),
                new Pair<>(i + 1, j),
                new Pair<>(i + 1, j + 1)
        ).parallel().forEach(c -> {
            if (c.x >= 0 && c.x < gameMode.getGridHeight() && c.y >= 0 && c.y < gameMode.getGridWidth()) {
                if (tiles[c.x][c.y].getContent() == TileContent.MINE)
                    near_mines.getAndIncrement();
            }
        });

        return near_mines.get();
    }

    /**
     * Execute an action on the game grid at a given coordinate.
     *
     * @param x          the row coordinate
     * @param y          the column coordinate
     * @param actionType the action to be executed
     * @return the result of an action.
     */
    public synchronized ActionResult action(int x, int y, ActionType actionType) {

        if (gameOverFlag || (tiles[x][y].getState() != TileState.NOT_VISITED && tiles[x][y].getState() != TileState.FLAGGED))
            return ActionResult.IGNORED;

        switch (actionType) {
            case VISIT:

                // always allow the first action
                if (firstActionFlag) {
                    if (tiles[x][y].getContent() == TileContent.MINE) {
                        Pair<Integer, Integer> point = findEmptyTile();
                        if (point != null) {
                            tiles[point.x][point.y].setContent(TileContent.MINE);
                        }

                        //move the mine somewhere else
                        tiles[x][y].setContent(TileContent.EMPTY);

                        // update the precomputed grid
                        precomputeGridContent();
                    }
                    firstActionFlag = false;
                }

                // game lost
                if (tiles[x][y].getContent() == TileContent.MINE) {
                    gameOverFlag = true;
                    gameLostFlag = true;
                    tiles[x][y].setState(TileState.EXPLODED);
                    duration = Duration.between(startedAt, Instant.now());
                    return ActionResult.EXPLOSION;
                }

                // expand the visited area if an empty tile is selected
                this.visitAndExpand(x, y);

                // check for game over (all tiles are visited or flagged correctly)
                if (this.toVisitCount == this.visitedCount) {
                    gameOverFlag = true;
                    duration = Duration.between(startedAt, Instant.now());
                    return ActionResult.GAME_OVER;
                }
                return ActionResult.OK;
            case FLAG:
                if (tiles[x][y].getState() == TileState.FLAGGED)
                    tiles[x][y].setState(TileState.NOT_VISITED);
                else if (tiles[x][y].getState() == TileState.NOT_VISITED)
                    tiles[x][y].setState(TileState.FLAGGED);
                return ActionResult.OK;
            default:
                return ActionResult.IGNORED;
        }
    }

    private Pair<Integer, Integer> findEmptyTile() {
        for (int i = 0; i < gameMode.getGridHeight(); i++) {
            for (int j = 0; j < gameMode.getGridWidth(); j++) {
                if (tiles[i][j].getContent() != TileContent.MINE) {
                    return new Pair<>(i, j);
                }
            }
        }
        return null;
    }

    private void visitAndExpand(int x, int y) {
        if (!(x >= 0 && x < this.gameMode.getGridHeight() && y >= 0 && y < this.gameMode.getGridWidth()))
            return;

        if (tiles[x][y].getState() == TileState.VISITED)
            return;

        tiles[x][y].setState(TileState.VISITED);
        visitedCount++;
        if (tiles[x][y].getContent() == TileContent.EMPTY) {
            visitAndExpand(x + 1, y);
            visitAndExpand(x - 1, y);
            visitAndExpand(x, y + 1);
            visitAndExpand(x, y - 1);
            visitAndExpand(x + 1, y + 1);
            visitAndExpand(x + 1, y - 1);
            visitAndExpand(x - 1, y + 1);
            visitAndExpand(x - 1, y - 1);
        }
    }

    /**
     * Returns the current state of the grid as a matrix of integer values representing each tile type.
     */
    public synchronized String toString() {
        String[] output = new String[gameMode.getGridHeight() * gameMode.getGridWidth()];

        for (int i = 0; i < gameMode.getGridHeight(); i++) {
            for (int j = 0; j < gameMode.getGridWidth(); j++) {
                String value;
                if (tiles[i][j].getState() == TileState.VISITED || (gameOverFlag && tiles[i][j].getState() != TileState.EXPLODED)) {
                    value = tiles[i][j].getContent().value;
                } else {
                    value = tiles[i][j].getState().value;
                }
                output[i * gameMode.getGridWidth() + j] = value;
            }
        }

        return String.join(" ", output);
    }

    public synchronized void resetGame() {
        gameLostFlag = false;
        gameOverFlag = false;
        firstActionFlag = true;
        visitedCount = 0;

        for (int i = 0; i < gameMode.getGridHeight(); i++) {
            for (int j = 0; j < gameMode.getGridWidth(); j++) {
                tiles[i][j].setState(TileState.NOT_VISITED);
            }
        }
    }

    public synchronized Tile[][] getTiles() {
        return tiles;
    }

    public synchronized void addPlayer(UUID newPlayerId) throws IllegalStateException {
        if (connectedPlayers.size() >= gameMode.getNumPlayers())
            throw new IllegalStateException("Game is already full");
        else
            connectedPlayers.add(newPlayerId);
    }

    public synchronized boolean containsPlayer(UUID playerId) {
        return connectedPlayers.contains(playerId);
    }

    public synchronized void removePlayer(UUID playerId) throws IllegalArgumentException {
        if (!connectedPlayers.contains(playerId))
            throw new IllegalArgumentException("Player not found");
        else
            connectedPlayers.remove(playerId);
    }

    public synchronized int getConnectedPlayersCount() {
        return connectedPlayers.size();
    }

    public synchronized GameMode getGameMode() {
        return gameMode;
    }

    public synchronized String getStartedAt() {
        return startedAt.toString();
    }

    public synchronized long getDuration() {
        return duration.toMillis();
    }

    public synchronized boolean isOver() {
        return gameOverFlag;
    }

    public synchronized boolean isLost() {
        return gameLostFlag;
    }
}
