package multiplayer.minesweeper.sessions;

import java.util.Date;

public class Session {
    private final String roomId;
    private final Date creationDate;
    private final String sessionName;
    private final GameMode gameMode;
    private final int gridWidth;
    private final int gridHeight;
    private final int numPlayers;
    private int numConnectedUsers;

    public Session(String roomId, String sessionName, GameMode gameMode) {
        this.roomId = roomId;
        this.sessionName = sessionName;
        this.gameMode = gameMode;
        this.numConnectedUsers = 0;
        this.gridWidth = gameMode.getGridWidth();
        this.gridHeight = gameMode.getGridHeight();
        this.numPlayers = gameMode.getGridWidth();
        this.creationDate = new Date();
    }

    public synchronized void addConnectedUsers() {
        numConnectedUsers++;
    }

    public synchronized void removeConnectedUsers() {
        numConnectedUsers--;
    }

    public String getRoomId() {
        return roomId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getSessionName() {
        return sessionName;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getNumConnectedUsers() { return numConnectedUsers; }

    public boolean isFull() {
        return numConnectedUsers >= gameMode.getNumPlayers();
    }

    public boolean isEmpty() { return numConnectedUsers == 0; }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
