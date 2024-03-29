package multiplayer.minesweeper.game;

public enum ActionType {
    FLAG("FLAG"),
    VISIT("VISIT");

    public final String label;

    ActionType(String label) {
        this.label = label;
    }
}
