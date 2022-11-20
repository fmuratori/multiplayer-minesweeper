package multiplayer.minesweeper.game;

public enum GameDifficulty {
    EASY(0.2),
    MEDIUM(0.4),
    HARD(0.6);

    public final double value;

    private GameDifficulty(double value) {
        this.value = value;
    }
}
