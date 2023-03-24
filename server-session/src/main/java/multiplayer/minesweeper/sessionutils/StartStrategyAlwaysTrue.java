package multiplayer.minesweeper.sessionutils;

/**
 * Trivial example of a game start behaviour. As soon as the check is executed, a game can start.
 *
 * This behaviour is used when a player connects to a single player game mode and for testing purpose.
 */
public class StartStrategyAlwaysTrue implements StartStrategy {

    public StartStrategyAlwaysTrue() {
    }
    @Override
    public boolean checkCondition() {
        return true;
    }
}
