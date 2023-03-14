package multiplayer.minesweeper.websocket.out;

public class PlayersCountMessage {

    private int connectedCount;
    private int maxPlayersCount;

    public PlayersCountMessage() {
    }

    public PlayersCountMessage(int count, int maxCount) {
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
