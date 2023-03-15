package multiplayer.minesweeper.websocket.out;

public class GameUpdateMessage {

    private String map;

    public GameUpdateMessage() {
    }

    public GameUpdateMessage(String map) {
        super();
        this.map = map;
    }

    public String getMap() {
        return map;
    }
}
