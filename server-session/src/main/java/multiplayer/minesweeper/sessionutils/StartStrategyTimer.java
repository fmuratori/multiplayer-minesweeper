package multiplayer.minesweeper.sessionutils;

import multiplayer.minesweeper.Controller;
import multiplayer.minesweeper.sessions.Session;

public class StartStrategyTimer implements StartStrategy {
    private boolean condition = false;
    public StartStrategyTimer(Session session, long waitTime) {
        long startedAt = System.currentTimeMillis();
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(waitTime);
                    condition = !session.isEmpty() && (System.currentTimeMillis() - startedAt) > waitTime;
                    if (condition) {
                        Controller.get().sendGameStartingRequest(session.getRoomId(), session);
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    @Override
    public boolean checkCondition() {
        return condition;
    }
}
