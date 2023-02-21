package multiplayer.minesweeper;

import io.vertx.core.Vertx;
import multiplayer.minesweeper.cli.CLIActions;
import multiplayer.minesweeper.game.GamesManager;
import multiplayer.minesweeper.rest.HttpServer;
import multiplayer.minesweeper.socket.SocketServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {    
    // public static String resolve(String containerName) throws UnknownHostException {
    //     String hostName = InetAddress.getByName(containerName).getHostName();
    //     String ipAddress = InetAddress.getByName(containerName).getHostAddress();
    //     System.out.println("Resolved container name: " + containerName);
    //     System.out.println("Resolved container hostname: " + hostName);
    //     System.out.println("Resolved container IP address: " + ipAddress);
    //     System.out.println("=====================================");
    //     return ipAddress;
    // }

    public static void main(String[] args) throws UnknownHostException {

        // String host = resolve("gameserver");

        GamesManager manager = new GamesManager();

        // start socket.io server
        SocketServer socketServer = new SocketServer(manager);
        socketServer.initialize(8004);

        // start http server
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = new HttpServer(vertx, 8003, manager);

        new Thread(new CLIActions(httpServer, socketServer)).start();
    }
}
