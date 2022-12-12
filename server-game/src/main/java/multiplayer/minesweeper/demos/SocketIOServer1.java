package multiplayer.minesweeper.demos;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import multiplayer.minesweeper.socket.SocketServer;

public class SocketIOServer1 {
    private SocketIOServer server;

    private SocketIOServer1() {}

    public void initialize(int port) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(port);
//        config.setAuthorizationListener(new AuthorizationListener() {
//            @Override
//            public boolean isAuthorized(HandshakeData data) {
//                return true;
//            }
//        });
        server = new SocketIOServer(config);

        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.start();

        System.out.println("SocketIO server started on port " + port);
    }

    private ConnectListener onConnected() {
        return new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("Socket ID["+client.getSessionId().toString()+"]  Connected to socket");
            }
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            System.out.println("Client["+client.getSessionId().toString()+"] - Disconnected from socket" );
        };
    }

    public static void main(String[] args) {
        new SocketIOServer1().initialize(8004);
    }
}
