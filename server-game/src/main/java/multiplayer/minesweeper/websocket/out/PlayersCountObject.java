package multiplayer.minesweeper.websocket.out;

public class PlayersCountObject {

    private int connectedCount;

    public PlayersCountObject() {}

    public PlayersCountObject(int count) {
        super();
        this.connectedCount = count;
    }

    public int getConnectedCount() {
        return connectedCount;
    }
}
