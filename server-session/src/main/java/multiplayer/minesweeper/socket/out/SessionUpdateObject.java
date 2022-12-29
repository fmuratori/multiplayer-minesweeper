package multiplayer.minesweeper.socket.out;

import multiplayer.minesweeper.sessions.Session;

public class SessionUpdateObject {
    private Session session;
    private String updateType;

    public SessionUpdateObject() {}

    public SessionUpdateObject(Session session, String updateType) {
        this.session = session;
        this.updateType = updateType;
    }

    public Session getSession() {
        return session;
    }

    public String getUpdateType() {
        return updateType;
    }
}
