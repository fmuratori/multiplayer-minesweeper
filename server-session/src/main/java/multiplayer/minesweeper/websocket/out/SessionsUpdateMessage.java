package multiplayer.minesweeper.websocket.out;

import multiplayer.minesweeper.sessions.Session;

import java.util.List;

public class SessionsUpdateMessage {
    private List<Session> sessions;

    public SessionsUpdateMessage() {
    }

    public SessionsUpdateMessage(List<Session> sessions) {
        this.sessions = sessions;
    }

    public List<Session> getSessions() {
        return sessions;
    }
}
