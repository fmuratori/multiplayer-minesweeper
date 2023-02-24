package multiplayer.minesweeper.socket.out;

public class GameOverObject extends GameUpdateObject {
    private long duration;

    public GameOverObject() {
        super();
    }
    public GameOverObject(String map, long duration) {
        super(map);
        this.duration = duration;
    }
    public long getDuration() {
        return duration;
    }
}
