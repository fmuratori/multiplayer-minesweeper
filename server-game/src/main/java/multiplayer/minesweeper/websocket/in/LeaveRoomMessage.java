package multiplayer.minesweeper.websocket.in;

public class LeaveRoomMessage {

    private String roomName;

    public LeaveRoomMessage() {
    }

    public LeaveRoomMessage(String roomName) {
        super();
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public String toString() {
        return "LeaveRoomObject{" +
                "roomName='" + roomName + '\'' +
                '}';
    }
}
