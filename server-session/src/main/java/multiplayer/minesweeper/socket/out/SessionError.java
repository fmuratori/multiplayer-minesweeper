package multiplayer.minesweeper.socket.out;

public class SessionError {
    private String message;

    public SessionError() {}
    public SessionError(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
