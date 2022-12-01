package multiplayer.minesweeper.socket;

public class ActionObject {

    private int xCoordinate;
    private int yCoordinate;

    private String action;

    public ActionObject() {
    }

    public ActionObject(int xCoordinate, int yCoordinate, String action) {
        super();
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.action = action;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }
    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }
    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String yCoordinate) {
        this.action = action;
    }

}
