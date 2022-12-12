package multiplayer.minesweeper.socket.in;

public class ActionObject {

    private int xCoordinate;
    private int yCoordinate;

    private String action;

    public ActionObject() {
    }

    public ActionObject(int xCoordinate, int yCoordinate, String action) {
        super();
        this.action = action;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ActionObject{" +
                "xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                ", action='" + action + '\'' +
                '}';
    }
}
