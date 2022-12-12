package multiplayer.minesweeper.socket.in;

public class MessageObject {

    private String message;

    public MessageObject() {
    }

    public MessageObject(String room_name) {
        super();
        this.message = room_name;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
