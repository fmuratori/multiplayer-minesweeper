package multiplayer.minesweeper.sessionutils;

import multiplayer.minesweeper.sessions.Session;

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
