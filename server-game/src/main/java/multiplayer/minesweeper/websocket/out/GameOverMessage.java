package multiplayer.minesweeper.websocket.out;

public class GameOverMessage extends GameUpdateMessage {
    private long duration;

    public GameOverMessage() {
        super();
    }

    public GameOverMessage(String map, long duration) {
        super(map);
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }
}
