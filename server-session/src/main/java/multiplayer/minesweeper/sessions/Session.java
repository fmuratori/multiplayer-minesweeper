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

    private boolean isFullFlag = false;

    public Session(String roomId, String sessionName, GameMode gameMode) {
        this.roomId = roomId;
        this.sessionName = sessionName;
        this.gameMode = gameMode;
        this.numConnectedUsers = 0;
        this.gridWidth = gameMode.getGridWidth();
        this.gridHeight = gameMode.getGridHeight();
        this.numPlayers = gameMode.getNumPlayers();
        this.creationDate = new Date();
    }

    public synchronized void addConnectedUsers() {

        numConnectedUsers++;
        if (numConnectedUsers == numPlayers)
            isFullFlag = true;
    }

    public synchronized void removeConnectedUsers() {
        numConnectedUsers--;

        if (numConnectedUsers < numPlayers)
            isFullFlag = false;
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

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public GameMode getGameMode() {
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
