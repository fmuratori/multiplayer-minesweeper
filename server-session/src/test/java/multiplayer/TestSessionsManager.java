package multiplayer;

import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestSessionsManager {
    private SessionsManager manager;

    @BeforeEach
    void initialize() {
        manager = new SessionsManager();
    }


    @Test
    void testAddSession() {
        assertTrue(manager.getOpenSessions().isEmpty());

        manager.addSession("test", "test", "mode1", 2, 4, 4);

        assertEquals(manager.getOpenSessions().size(), 1);

        try {
            manager.addSession("test", "test", "mode1", 2, 4, 4);
        } catch (IllegalArgumentException ignored){}

        assertEquals(manager.getOpenSessions().size(), 1);
    }
    @Test
    void testRemoveSession() {
        manager.addSession("test", "test", "mode1", 2, 4, 4);
        manager.addSession("test2", "test", "mode1", 2, 4, 4);

        manager.removeSession("test");
        assertEquals(manager.getOpenSessions().size(), 1);
        manager.removeSession("test");
        assertEquals(manager.getOpenSessions().size(), 1);
        manager.removeSession("test2");
        assertEquals(manager.getOpenSessions().size(), 0);
    }

    @Test
    void testGetAvailableSession() {
        Session s1 = manager.addSession("test", "test", "mode1", 2, 4, 4);
        Session s2 = manager.addSession("test2", "test", "mode1", 1, 4, 4);

        assertEquals(manager.getOpenSessions().size(), 2);
        s2.addConnectedUsers();

        assertEquals(manager.getOpenSessions().size(), 1);
        s1.addConnectedUsers();
        assertEquals(manager.getOpenSessions().size(), 1);
        s1.addConnectedUsers();
        assertEquals(manager.getOpenSessions().size(), 0);
    }
    @Test
    void testGetSessionByMode() {
        Session s1 = manager.addSession("test", "test", "mode1", 2, 4, 4);
        Session s2 = manager.addSession("test2", "test", "mode1", 1, 4, 4);
        Session s3 = manager.addSession("test3", "test", "mode2", 1, 4, 4);

        assertEquals(manager.getOpenSessionsByMode("mode1").size(), 2);
        assertEquals(manager.getOpenSessionsByMode("mode2").size(), 1);

        s1.addConnectedUsers();
        s2.addConnectedUsers();
        s3.addConnectedUsers();
        assertEquals(manager.getOpenSessionsByMode("mode1").size(), 1);
        assertEquals(manager.getOpenSessionsByMode("mode2").size(), 0);
    }
}
