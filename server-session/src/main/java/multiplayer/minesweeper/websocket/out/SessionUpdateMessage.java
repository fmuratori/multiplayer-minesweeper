package multiplayer.minesweeper.websocket.out;

import multiplayer.minesweeper.sessions.Session;

public class SessionUpdateMessage {
    private Session session;
    private SessionUpdateType updateType;

    public SessionUpdateMessage() {
    }

    public SessionUpdateMessage(Session session, SessionUpdateType updateType) {
        this.session = session;
        this.updateType = updateType;
    }

    public Session getSession() {
        return session;
    }

    public String getUpdateType() {
        return updateType.getValue();
    }
}
