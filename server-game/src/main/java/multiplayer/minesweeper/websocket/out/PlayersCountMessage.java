package multiplayer.minesweeper.websocket.out;

public class PlayersCountMessage {

    private int connectedCount;

    public PlayersCountMessage() {
    }

    public PlayersCountMessage(int count) {
        super();
        this.connectedCount = count;
    }

    public int getConnectedCount() {
        return connectedCount;
    }
}
