package multiplayer.minesweeper.sessions;

import multiplayer.minesweeper.sessionutils.StartStrategy;
import multiplayer.minesweeper.sessionutils.StartStrategyMaxPlayers;

import java.util.Date;

/**
 * This class represents a session of players. Whenever players are waiting for a game to start,
 * a session object is initialized. Interestingly, no data or identifier about the players is kept.
 * They are grouped together thanks to the Socket.IO rooms logic. Here only the Socket.IO room id is needed.
 *
 * Also, a Session starting logic may vary from game mode to game mode. The starting logic is refactored
 * with a Strategy design pattern.
 */
public class Session {
    private final String roomId;
    private final String sessionName;
    private final String gameMode;
    private int numConnectedUsers;
    private final int numPlayers;
    private final int gridWidth;
    private final int gridHeight;
    private final Date creationDate;
    private final StartStrategy behaviour;

    public Session(String roomId, String sessionName, String gameMode, int numPlayers, int gridWidth, int gridHeight) {
        this.roomId = roomId;
        this.sessionName = sessionName;
        this.gameMode = gameMode;
        this.numConnectedUsers = 0;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.numPlayers = numPlayers;
        this.creationDate = new Date();
        this.behaviour = new StartStrategyMaxPlayers(this);
    }

    public synchronized void addConnectedUsers() {
        if (!isFull()) numConnectedUsers++;
    }

    public synchronized void removeConnectedUsers() {
        if (!isEmpty()) numConnectedUsers--;
    }

    public synchronized int getNumPlayers() {
        return numPlayers;
    }

    public synchronized String getGameMode() {
        return gameMode;
    }
    public synchronized String getSessionName() {
        return sessionName;
    }
    public synchronized String getRoomId() {
        return roomId;
    }

    public synchronized int getNumConnectedUsers() {
        return numConnectedUsers;
    }

    public synchronized boolean isFull() {
        return numConnectedUsers == numPlayers;
    }

    public synchronized boolean isEmpty() {
        return numConnectedUsers == 0;
    }

    public synchronized boolean checkStartCondition() {
        return this.behaviour.checkCondition();
    }
}
