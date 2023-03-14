package multiplayer.minesweeper.websocket.out;

public class SessionErrorMessage {
    private String message;

    public SessionErrorMessage() {
    }

    public SessionErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
