package multiplayer.minesweeper.websocket.in;

public class JoinRoomMessage {

    private String roomName;

    public JoinRoomMessage() {
    }

    public JoinRoomMessage(String roomName) {
        super();
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public String toString() {
        return "JoinRoomObject{" +
                "roomName='" + roomName + '\'' +
                '}';
    }
}
