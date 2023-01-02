package multiplayer.minesweeper.sessions;

import java.util.*;
import java.util.stream.Collectors;

public class SessionsManager {
    private final Map<String, Session> sessions = new HashMap<>();

    public SessionsManager() {
    }

    public Optional<Session> getSession(String roomId) {
        if (!sessions.containsKey(roomId))
            return Optional.empty();
        else
            return Optional.of(sessions.get(roomId));
    }

    public Session addSession(String roomId, String sessionName, GameMode mode) {
        Session newSession = new Session(roomId, sessionName, mode);
        sessions.put(roomId, newSession);
        return newSession;
    }

    public void removeSession(String roomId) {
        sessions.remove(roomId);
    }

    public List<Session> getSessionsByMode(GameMode mode) {
        return sessions
                .values()
                .stream()
                .filter(s -> s.getGameMode() == mode)
                .collect(Collectors.toList());
    }

    public List<Session> getOpenSessions() {
        return sessions.values().stream().filter(s -> !s.isFull()).collect(Collectors.toList());
    }
}