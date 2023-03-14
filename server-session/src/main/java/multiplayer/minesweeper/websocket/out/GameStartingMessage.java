package multiplayer.minesweeper.websocket.out;

import multiplayer.minesweeper.sessions.Session;

public class GameStartingMessage {

    private String roomName;
    private Session session;

    public GameStartingMessage() {
    }

    public GameStartingMessage(String roomName, Session session) {
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
