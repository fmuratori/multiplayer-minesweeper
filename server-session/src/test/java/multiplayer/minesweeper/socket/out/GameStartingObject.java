package multiplayer.minesweeper.socket.out;

import multiplayer.minesweeper.sessions.Session;

public class GameStartingObject {

    private String roomName;
    private Session session;

    public GameStartingObject(String roomName, Session session) {
        this.roomName = roomName;
        this.session = session;
    }

    public String getRoomName() {
        return roomName;
    }
    public Session getSession() {
        return session;
    }
}
