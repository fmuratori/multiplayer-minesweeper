package multiplayer.minesweeper.observer;

import multiplayer.minesweeper.observer.events.LeaveSessionEvent;
import multiplayer.minesweeper.observer.events.JoinSessionEvent;
import multiplayer.minesweeper.rest.client.HTTPClient;
import multiplayer.minesweeper.sessions.Session;
import multiplayer.minesweeper.sessions.SessionsManager;
import multiplayer.minesweeper.socket.SocketServer;
import multiplayer.minesweeper.socket.out.*;

import java.util.Optional;

public class SocketObserver implements Observer {

    @Override
    public void notifyEvent(Object event) {
        if (event instanceof JoinSessionEvent) {
            handleJoinSessionEvent((JoinSessionEvent) event);
        } else if (event instanceof LeaveSessionEvent) {
            handleLeaveSessionEvent((LeaveSessionEvent) event);
        }
    }

    private void handleLeaveSessionEvent(LeaveSessionEvent event) {
//        System.out.println("[Socket.IO] - Socket ID [" + client.getSessionId().toString() + "] - " + data.toString());

        String roomName = data.getRoomName();
        Optional<Session> session = sessionsManager.getSession(roomName);
        if (session.isEmpty()) {
            sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session not found"));
        } else if (session.get().isEmpty()) {
            sessionNamespace.getRoomOperations(roomName).sendEvent("session_error", new SessionError("Session is empty"));
        } else {
            client.leaveRoom(roomName);

            session.get().removeConnectedUsers();

            // send new user connection to all connected users
            int connectedClients = server.getRoomOperations(roomName).getClients().size();
            server.getRoomOperations(roomName).sendEvent("players_count_update", new PlayersCountObject(connectedClients, session.get().getNumPlayers()));
            browseNamespace.getBroadcastOperations().sendEvent("session_update", new SessionUpdateObject(session.get(), SessionUpdateType.REMOVED_USER));
        }
    }

    void handleJoinSessionEvent(JoinSessionEvent event) {

        Optional<Session> session = SessionsManager.get().getSession(event.getRoomName());
        if (session.isEmpty()) {
            SocketServer.get().sendSessionError(new SessionError("Session not found"));
        } else if (session.get().isFull()) {
            SocketServer.get().sendSessionError(new SessionError("Session is full"));
        } else {
            session.get().addConnectedUsers();

            // add user to one game session
            SocketServer.get().joinRoom(event.getRoomName(),
                    event.getClient(),
                    new PlayersCountObject(session.get().getNumConnectedUsers(), session.get().getNumPlayers()));

            if (session.get().getNumConnectedUsers() == session.get().getNumPlayers()) {
                System.out.println("[Socket.IO] - Starting game for room " + event.getRoomName());
                HTTPClient.get().sendGameRequest(event.getRoomName(), session.get());

                SocketServer.get().sendSessionUpdate(new SessionUpdateObject(session.get(), SessionUpdateType.GAME_STARTING));
            } else {
                SocketServer.get().sendSessionUpdate(new SessionUpdateObject(session.get(), SessionUpdateType.ADDED_USER));
            }
        }
    }
}
