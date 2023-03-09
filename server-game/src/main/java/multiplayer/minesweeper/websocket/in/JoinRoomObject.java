package multiplayer.minesweeper.websocket.in;

public class JoinRoomObject {

    private String roomName;

    public JoinRoomObject() {}

    public JoinRoomObject(String roomName) {
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
