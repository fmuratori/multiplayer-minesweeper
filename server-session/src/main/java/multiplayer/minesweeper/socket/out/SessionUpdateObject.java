package multiplayer.minesweeper.socket.out;

import multiplayer.minesweeper.sessions.Session;

public class SessionUpdateObject {
    private Session session;
    private SessionUpdateType updateType;

    public SessionUpdateObject() {}

    public SessionUpdateObject(Session session, SessionUpdateType updateType) {
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
