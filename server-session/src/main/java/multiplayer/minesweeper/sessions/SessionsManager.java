package multiplayer.minesweeper.sessions;

import java.util.*;
import java.util.stream.Collectors;

public class SessionsManager {
    private final Map<String, Session> sessions = new HashMap<>();

    public SessionsManager() {
    }

    public synchronized Optional<Session> getSession(String roomId) {
        if (!sessions.containsKey(roomId))
            return Optional.empty();
        else
            return Optional.of(sessions.get(roomId));
    }

    public synchronized Session addSession(String roomId, String sessionName, String mode, int numPlayers, int gridWidth, int gridHeight) {
        var newSession = new Session(roomId, sessionName, mode, numPlayers, gridWidth, gridHeight);
        sessions.put(roomId, newSession);
        return newSession;
    }

    public synchronized void removeSession(String roomId) {
        sessions.remove(roomId);
    }

    public synchronized List<Session> getOpenSessionsByMode(String mode) {
        return sessions
                .values()
                .stream()
                .filter(s -> Objects.equals(s.getGameMode(), mode) && !s.isFull())
                .collect(Collectors.toList());
    }

    public synchronized List<Session> getOpenSessions() {
        return sessions.values().stream().filter(s -> !s.isFull()).collect(Collectors.toList());
    }
}
