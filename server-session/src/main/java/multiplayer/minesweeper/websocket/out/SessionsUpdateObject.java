package multiplayer.minesweeper.websocket.out;

import multiplayer.minesweeper.sessions.Session;

import java.util.List;

public class SessionsUpdateObject {
    private List<Session> sessions;

    public SessionsUpdateObject() {}
    public SessionsUpdateObject(List<Session> sessions) {
        this.sessions = sessions;
    }

    public List<Session> getSessions() {
        return sessions;
    }
}
