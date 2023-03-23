package multiplayer.minesweeper;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class TestController {

    @Test
    void testNewSessionRequest() {
        Controller.get().handleNewSessionRequest("TEST_NAME", "MEDIUM", 2, 8, 8)
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    assertTrue(object.containsKey("roomId"));

                    var status = (String) object.get("status");
                    assertEquals("CREATED", status);
                } ).join();
    }

    @Test
    void testSimpleJoinSessionRequest() {
        // join session and no game start
        Controller.get().handleNewSessionRequest("TEST_NAME", "SMALL", 2, 8, 8)
            .thenCompose((Map<String, Object> object) -> Controller.get()
                    .handleJoinSession((String) object.get("roomId")))
            .thenAccept((Map<String, Object> object) -> {
                assertTrue(object.containsKey("status"));
                assertTrue(object.containsKey("session"));
                assertTrue(object.containsKey("num_connections"));
                var status = (String) object.get("status");
                assertEquals("JOINED", status);
            }).join();

        // join session and game start
        Controller.get().handleNewSessionRequest("TEST_NAME", "SMALL", 1, 8, 8)
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleJoinSession((String) object.get("roomId")))
                .thenAccept((Map<String, Object> object) -> {
                    var status = (String) object.get("status");
                    assertEquals("GAME_STARTING", status);
                }).join();
    }

    @Test
    void testSimpleJoinSessionErrorRequest() {
        // session not found
        Controller.get().handleJoinSession("ERROR_ID")
                .thenAccept((Map<String, Object> object) -> {
                    var status = (String) object.get("status");
                    assertEquals("NO_SESSION", status);
                }).join();

        // session is already full
        AtomicReference<String> roomId = new AtomicReference<>();
        Controller.get().handleNewSessionRequest("TEST_NAME", "SMALL", 1, 8, 8)
                .thenCompose((Map<String, Object> object) -> {
                    roomId.set((String) object.get("roomId"));
                    // two consecutive connections, the second one must fail
                    Controller.get()
                            .handleJoinSession(roomId.get()).join();
                    return Controller.get()
                            .handleJoinSession(roomId.get());
                }).thenAccept((Map<String, Object> object) -> {
                    var status = (String) object.get("status");
                    assertEquals("FULL_SESSION", status);
                }).join();
    }

    @Test
    void testSimpleLeaveSessionRequest() {
        // join and leave session
        AtomicReference<String> roomId = new AtomicReference<>();
        Controller.get().handleNewSessionRequest("TEST_NAME", "SMALL", 2, 8, 8)
                .thenCompose((Map<String, Object> object) -> {
                    roomId.set((String) object.get("roomId"));
                    return Controller.get().handleJoinSession(roomId.get());
                })
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleLeaveSession(roomId.get()))
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("LEFT", status);
                }).join();

        // join session, game start and leave session
        Controller.get().handleNewSessionRequest("TEST_NAME", "SMALL", 1, 8, 8)
                .thenCompose((Map<String, Object> object) -> {
                    roomId.set((String) object.get("roomId"));
                    return Controller.get().handleJoinSession(roomId.get());
                })
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleLeaveSession(roomId.get()))
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("LEFT", status);
                }).join();
    }

    @Test
    void testLeaveSessionErrorRequest() {
        // leave an empty session
        AtomicReference<String> roomId = new AtomicReference<>();
        Controller.get().handleNewSessionRequest("TEST_NAME", "SMALL", 1, 8, 8)
                .thenCompose((Map<String, Object> object) -> {
                    roomId.set((String) object.get("roomId"));
                    return Controller.get().handleJoinSession(roomId.get());
                })
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleLeaveSession(roomId.get()))
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleLeaveSession(roomId.get()))
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("EMPTY_SESSION", status);
                }).join();

        // leave session never created
        Controller.get().handleLeaveSession("ERROR_ID")
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("NO_SESSION", status);
                }).join();
    }
}
