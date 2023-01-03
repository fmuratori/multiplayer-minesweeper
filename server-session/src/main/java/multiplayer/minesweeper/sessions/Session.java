package multiplayer.minesweeper.sessions;

import java.util.Date;

public class Session {
    private final String roomId;
    private final Date creationDate;
    private final String sessionName;
    private final String gameMode;
    private final int numPlayers;
    private final int gridWidth;
    private final int gridHeight;
    private int numConnectedUsers;

    private boolean isFullFlag = false;

    public Session(String roomId, String sessionName, String gameMode, int numPlayers, int gridWidth, int gridHeight) {
        this.roomId = roomId;
        this.sessionName = sessionName;
        this.gameMode = gameMode;
        this.numConnectedUsers = 0;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.numPlayers = numPlayers;
        this.creationDate = new Date();
    }

    public synchronized void addConnectedUsers() {

        numConnectedUsers++;
        if (numConnectedUsers == numPlayers) isFullFlag = true;
    }

    public synchronized void removeConnectedUsers() {
        numConnectedUsers--;

        if (numConnectedUsers < numPlayers) isFullFlag = false;
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

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public String getGameMode() {
        return gameMode;
    }

    public synchronized int getNumConnectedUsers() {
        return numConnectedUsers;
    }

    public synchronized boolean isFull() {
        return isFullFlag;
    }

    public synchronized boolean isEmpty() {
        return numConnectedUsers == 0;
    }

}
