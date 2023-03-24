package multiplayer.minesweeper.sessionutils;

import multiplayer.minesweeper.sessions.Session;

/**
 * Implementation of a game starting behaviour. As soon as the required amount of players is connected to
 * a session, the game is initialized. This check is executed only when a player joins a session.
 */
public class StartStrategyMaxPlayers implements StartStrategy {
    private final Session session;

    public StartStrategyMaxPlayers(Session session) {
        this.session = session;
    }
    @Override
    public boolean checkCondition() {
        return this.session.isFull();
    }
}
