package multiplayer.minesweeper.sessions;

import java.util.*;
import java.util.stream.Collectors;

public class SessionsManager {
    private static final SessionsManager instance = new SessionsManager();
    public static SessionsManager get() {return instance;}
    private final Map<String, Session> sessions = new HashMap<>();

    private SessionsManager() {}

    public Optional<Session> getSession(String roomId) {
        if (!sessions.containsKey(roomId))
            return Optional.empty();
        else
            return Optional.of(sessions.get(roomId));
    }

    public Session addSession(String roomId, String sessionName, String mode, int numPlayers, int gridWidth, int  gridHeight) {
        Session newSession = new Session(roomId, sessionName, mode, numPlayers, gridWidth, gridHeight);
        sessions.put(roomId, newSession);
        return newSession;
    }

    public void removeSession(String roomId) {
        sessions.remove(roomId);
    }

    public List<Session> getOpenSessionsByMode(String mode) {
        return sessions
                .values()
                .stream()
                .filter(s -> Objects.equals(s.getGameMode(), mode) && !s.isFull())
                .collect(Collectors.toList());
    }

    public List<Session> getOpenSessions() {
        return sessions.values().stream().filter(s -> !s.isFull()).collect(Collectors.toList());
    }
}
