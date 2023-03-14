package multiplayer.minesweeper.sessionutils;

public class StartStrategyAlwaysTrue implements StartStrategy {

    public StartStrategyAlwaysTrue() {
    }
    @Override
    public boolean checkCondition() {
        return true;
    }
}
