package multiplayer.minesweeper.websocket.out;

public class PlayersCountObject {

    private int connectedCount;
    private int maxPlayersCount;

    public PlayersCountObject() {
    }

    public PlayersCountObject(int count, int maxCount) {
        super();
        this.connectedCount = count;
        this.maxPlayersCount = maxCount;
    }

    public int getConnectedCount() {
        return connectedCount;
    }

    public int getMaxPlayersCount() {
        return maxPlayersCount;
    }
}
