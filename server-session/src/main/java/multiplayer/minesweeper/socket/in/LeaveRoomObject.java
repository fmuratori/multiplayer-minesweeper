package multiplayer.minesweeper.socket.in;

public class LeaveRoomObject {

    private String roomName;

    public LeaveRoomObject() {
    }

    public LeaveRoomObject(String roomName) {
        super();
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public String toString() {
        return "ExitRoomObject{" +
                "roomName='" + roomName + '\'' +
                '}';
    }
}
