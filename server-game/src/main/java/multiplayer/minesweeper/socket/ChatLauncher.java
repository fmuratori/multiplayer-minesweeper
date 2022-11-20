package multiplayer.minesweeper.socket;

import com.corundumstudio.socketio.*;

public class ChatLauncher {

    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
        server.addEventListener("chatevent", ChatObject.class, (client, data, ackRequest) -> {
            // broadcast messages to all clients
            server.getBroadcastOperations().sendEvent("chatevent", data);
        });

        server.start();

        Thread.sleep(50000);

        server.stop();
    }

}
