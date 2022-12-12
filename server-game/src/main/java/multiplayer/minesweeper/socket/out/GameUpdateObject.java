package multiplayer.minesweeper.socket.out;

public class GameUpdateObject {

    private String map;

    public GameUpdateObject() {}

    public GameUpdateObject(String map) {
        super();
        this.map = map;
    }

    public String getMap() {
        return map;
    }
}