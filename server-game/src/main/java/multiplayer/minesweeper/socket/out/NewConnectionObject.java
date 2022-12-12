package multiplayer.minesweeper.socket.out;

public class NewConnectionObject {

    private int connectedCount;

    public NewConnectionObject() {}

    public NewConnectionObject(int count) {
        super();
        this.connectedCount = count;
    }

    public int getConnectedCount() {
        return connectedCount;
    }
}
