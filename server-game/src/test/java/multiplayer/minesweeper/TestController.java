package multiplayer.minesweeper;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class TestController {
    @Test
    void testGameModesRequest() {
        Controller.get().handleGameModesRequest()
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("LIST", status);

                    assertTrue(object.containsKey("gameModes"));
                } ).join();
    }
    @Test
    void testNewGameRequestError() {
        Controller.get().handleNewGameRequest("error_game_type")
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));

                    var status = (String) object.get("status");
                    assertEquals("GAME_MODE_ERROR", status);
                } ).join();
    }
    @Test
    void testNewGameRequestSuccess() {
        Controller.get().handleNewGameRequest("SMALL")
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));

                    var status = (String) object.get("status");
                    assertEquals("CREATED", status);
                    assertTrue(object.containsKey("gameId"));
                }).join();
    }
    @Test
    void testSimpleJoinGameRequest() {
        Controller.get().handleNewGameRequest("SMALL")
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleJoinRoomRequest(
                                (String) object.get("gameId"), UUID.randomUUID()))
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    assertTrue(object.containsKey("map"));
                    assertTrue(object.containsKey("startedAt"));
                    assertTrue(object.containsKey("gameMode"));
                    assertTrue(object.containsKey("playersCount"));
                    var status = (String) object.get("status");
                    assertEquals("JOINED", status);
                    var playersCount = (int) object.get("playersCount");
                    assertEquals(1, playersCount);
                }).join();
    }
    @Test
    void testConcurrentJoinGameRequest() {
        var promise = Controller.get().handleNewGameRequest("LARGE");
        List<CompletableFuture<Void>> promises = new ArrayList<>();
        for (var i = 0; i < 4; i++) {
            promises.add(
                    promise
                            .thenCompose((Map<String, Object> object) -> Controller.get()
                                    .handleJoinRoomRequest(
                                            (String) object.get("gameId"),
                                            UUID.randomUUID()))
                            .thenAccept((Map<String, Object> object) -> {
                                var status = (String) object.get("status");
                                assertEquals("JOINED", status);
                            }));
        }
        CompletableFuture
                .allOf(promises.toArray(new CompletableFuture[0])).join();
    }

    @Test
    void testJoinErrorGameRequest() {
        var promise = Controller.get().handleNewGameRequest("LARGE");
        List<CompletableFuture<Void>> promises = new ArrayList<>();
        for (var i = 0; i < 4; i++) {
            promises.add(
                    promise
                            .thenCompose((Map<String, Object> object) -> Controller.get()
                                    .handleJoinRoomRequest(
                                            (String) object.get("gameId"),
                                            UUID.randomUUID()))
                            .thenAccept((Map<String, Object> object) -> {
                                var status = (String) object.get("status");
                                assertEquals("JOINED", status);
                            }));
        }
        CompletableFuture
                .allOf(promises.toArray(new CompletableFuture[0])).join();

        promise
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleJoinRoomRequest(
                                (String) object.get("gameId"),
                                UUID.randomUUID()))
                .thenAccept((Map<String, Object> object) -> {
                    var status = (String) object.get("status");
                    assertEquals("JOIN_ERROR", status);
                }).join();
    }

    @Test
    void testSimpleLeaveGameRequest() {
        UUID userUUID = UUID.randomUUID();
        AtomicReference<String> gameId = new AtomicReference<>();
        Controller.get().handleNewGameRequest("SMALL")
                .thenCompose(
                        (Map<String, Object> object) -> {
                        gameId.set((String) object.get("gameId"));
                        return Controller.get()
                                .handleJoinRoomRequest(
                                        gameId.get(), userUUID);
                    })
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleLeaveRoomRequest(
                                gameId.get(), userUUID))
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    assertTrue(object.containsKey("playersCount"));
                    var status = (String) object.get("status");
                    assertEquals("LEFT", status);
                    var playersCount = (int) object.get("playersCount");
                    assertEquals(0, playersCount);
                }).join();
    }

    @Test
    void testDisconnectMessage() {
        UUID userUUID = UUID.randomUUID();
        AtomicReference<String> gameId = new AtomicReference<>();
        Controller.get().handleNewGameRequest("MEDIUM")
                .thenCompose(
                        (Map<String, Object> object) -> {
                            gameId.set((String) object.get("gameId"));
                            return Controller.get()
                                    .handleJoinRoomRequest(
                                            gameId.get(), userUUID);
                        })
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleClientDisconnect(userUUID))
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("GAME_DELETED", status);
                }).join();
    }
    @Test
    void testDisconnectMessageWithError() {
        UUID userUUID = UUID.randomUUID();
        Controller.get().handleNewGameRequest("MEDIUM")
                .thenCompose((Map<String, Object> object) -> Controller.get()
                        .handleClientDisconnect(userUUID))
                .thenAccept((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("GAME_NOT_FOUND", status);
                }).join();
    }

    @Test
    void testConcurrentLeaveGameRequest() {
        // create a game
        AtomicReference<String> gameId = new AtomicReference<>();
        Controller.get().handleNewGameRequest("LARGE")
                .thenAccept(
                    (Map<String, Object> object) -> {
                    gameId.set((String) object.get("gameId"));
                }).join();

        // join room
        List<UUID> users = new ArrayList<>();
        List<CompletableFuture<Map<String, Object>>> joinPromises = new ArrayList<>();
        for (var i = 0; i < 4; i++) {
            UUID userUUID = UUID.randomUUID();
            users.add(userUUID);
            joinPromises.add(Controller.get()
                    .handleJoinRoomRequest(
                            gameId.get(), userUUID));
        }
        CompletableFuture
                .allOf(joinPromises.toArray(new CompletableFuture[0])).join();

        // leave room
        List<CompletableFuture<Void>> leavePromises = new ArrayList<>();
        for (var userID : users) {
            leavePromises.add(Controller.get()
                    .handleLeaveRoomRequest(
                            gameId.get(), userID)
                    .thenAccept((Map<String, Object> object) -> {
                        var status = (String) object.get("status");
                        assertEquals("LEFT", status);
                    }));
        }
        CompletableFuture
                .allOf(leavePromises.toArray(new CompletableFuture[0])).join();

    }
    @Test
    void testSimpleActionsRequests() {
        // create a game
        AtomicReference<String> gameId = new AtomicReference<>();
        Controller.get().handleNewGameRequest("LARGE")
                .thenAccept(
                        (Map<String, Object> object) -> {
                            gameId.set((String) object.get("gameId"));
                        }).join();
        // join room
        UUID userUUID = UUID.randomUUID();
        Controller.get()
                .handleJoinRoomRequest(
                        gameId.get(), userUUID).join();

        // check action type error
        Controller.get()
                .handleActionRequest(gameId.get(), "ERROR_ACTION", 0, 0)
                .thenAccept(((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("ACTION_ERROR", status);
                })).join();


        // check game id error
        Controller.get()
                .handleActionRequest("GAME_ERROR", "VISIT", 0, 0)
                .thenAccept(((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("GAME_NOT_FOUND", status);
                })).join();

        // check good action
        Controller.get()
                .handleActionRequest(gameId.get(), "VISIT", 0, 0)
                .thenAccept(((Map<String, Object> object) -> {
                    assertTrue(object.containsKey("status"));
                    var status = (String) object.get("status");
                    assertEquals("EXECUTED", status);
                })).join();
    }
}
