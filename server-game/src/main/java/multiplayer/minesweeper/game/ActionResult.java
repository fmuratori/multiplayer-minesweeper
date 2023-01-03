package multiplayer.minesweeper.game;

public enum ActionResult {
    OK("OK"),
    EXPLOSION("EXPLOSION"),
    IGNORED("IGNORED"),
    GAME_OVER("GAME_OVER");

    public final String label;

    ActionResult(String label) {
        this.label = label;
    }
}
