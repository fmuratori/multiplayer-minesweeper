package multiplayer;

import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestSessionsManager {

    @Test
    void testAddSession() {
        assertTrue(SessionsManager.get().getOpenSessions().isEmpty());

        SessionsManager.get().addSession("test", "test", "mode1", 2, 4, 4);

        assertEquals(SessionsManager.get().getOpenSessions().size(), 1);

        try {
            SessionsManager.get().addSession("test", "test", "mode1", 2, 4, 4);
        } catch (IllegalArgumentException ignored){}

        assertEquals(SessionsManager.get().getOpenSessions().size(), 1);
    }
    @Test
    void testRemoveSession() {
        SessionsManager.get().addSession("test", "test", "mode1", 2, 4, 4);
        SessionsManager.get().addSession("test2", "test", "mode1", 2, 4, 4);

        SessionsManager.get().removeSession("test");
        assertEquals(SessionsManager.get().getOpenSessions().size(), 1);
        SessionsManager.get().removeSession("test");
        assertEquals(SessionsManager.get().getOpenSessions().size(), 1);
        SessionsManager.get().removeSession("test2");
        assertEquals(SessionsManager.get().getOpenSessions().size(), 0);
    }

    @Test
    void testGetAvailableSession() {
        Session s1 = SessionsManager.get().addSession("test", "test", "mode1", 2, 4, 4);
        Session s2 = SessionsManager.get().addSession("test2", "test", "mode1", 1, 4, 4);

        assertEquals(SessionsManager.get().getOpenSessions().size(), 2);
        s2.addConnectedUsers();

        assertEquals(SessionsManager.get().getOpenSessions().size(), 1);
        s1.addConnectedUsers();
        assertEquals(SessionsManager.get().getOpenSessions().size(), 1);
        s1.addConnectedUsers();
        assertEquals(SessionsManager.get().getOpenSessions().size(), 0);
    }
    @Test
    void testGetSessionByMode() {
        Session s1 = SessionsManager.get().addSession("test", "test", "mode1", 2, 4, 4);
        Session s2 = SessionsManager.get().addSession("test2", "test", "mode1", 1, 4, 4);
        Session s3 = SessionsManager.get().addSession("test3", "test", "mode2", 1, 4, 4);

        assertEquals(SessionsManager.get().getOpenSessionsByMode("mode1").size(), 2);
        assertEquals(SessionsManager.get().getOpenSessionsByMode("mode2").size(), 1);

        s1.addConnectedUsers();
        s2.addConnectedUsers();
        s3.addConnectedUsers();
        assertEquals(SessionsManager.get().getOpenSessionsByMode("mode1").size(), 1);
        assertEquals(SessionsManager.get().getOpenSessionsByMode("mode2").size(), 0);
    }
}
