package multiplayer;

import multiplayer.minesweeper.sessions.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSession {
    private final int NUM_PLAYERS = 2;
    private Session session;

    @BeforeEach
    void initialize() {
        session = new Session("test", "name", "mode",
                NUM_PLAYERS, 4, 4);
    }


    @Test
    public void testUsersConnection() {
        assertTrue(session.isEmpty());

        session.addConnectedUsers();

        assertFalse(session.isEmpty());
        assertFalse(session.isFull());

        session.addConnectedUsers();

        assertTrue(session.isFull());
    }

    @Test
    public void testUsersDisconnection() {
        session.addConnectedUsers();
        session.removeConnectedUsers();

        assertFalse(session.isFull());
        assertTrue(session.isEmpty());
    }

    @Test
    public void testUsersConnectionOverLimit() {
        session.addConnectedUsers();
        session.addConnectedUsers();

        assertTrue(session.isFull());

        session.addConnectedUsers();
        assertTrue(session.isFull());

        assertEquals(NUM_PLAYERS,
                session.getNumConnectedUsers());
    }

    @Test
    public void testUsersDisconnectionUnderLimit() {
        session.removeConnectedUsers();
        assertTrue(session.isEmpty());
        assertFalse(session.isFull());

        session.addConnectedUsers();

        session.removeConnectedUsers();
        session.removeConnectedUsers();

        assertTrue(session.isEmpty());
        assertEquals(session.getNumConnectedUsers(), 0);
    }
}
