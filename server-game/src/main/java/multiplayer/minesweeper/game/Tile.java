package multiplayer.minesweeper.game;

public class Tile {
    private TileContent content;
    private TileState state;

    public Tile(TileContent content, TileState state) {
        this.content = content;
        this.state = state;
    }

    public TileState getState() {
        return state;
    }

    public void setState(TileState newState) {
        state = newState;
    }

    public TileContent getContent() {
        return content;
    }

    public void setContent(TileContent newContent) {
        content = newContent;
    }
}
