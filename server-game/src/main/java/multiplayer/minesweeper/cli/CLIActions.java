package multiplayer.minesweeper.cli;

import multiplayer.minesweeper.socket.SocketServer;
import multiplayer.minesweeper.rest.HttpServer;

import java.util.Scanner;

public class CLIActions implements Runnable {
    private final HttpServer restServer;
    private final SocketServer socketServer;
    private boolean running = true;

    public CLIActions(HttpServer restServer, SocketServer socketServer) {
        this.restServer = restServer;
        this.socketServer = socketServer;
    }

    @Override
    public void run() {
        System.out.println("[CLI] - Starting CLI");
        Scanner scanner = new Scanner(System.in);
        while (running) {
            String action = scanner.nextLine();
            System.out.println("[CLI] - Received CLI message: " + action);

            if (action.equals("EXIT")) {
                System.out.println("[CLI] - Closing active servers ...");
                running = false;
                socketServer.close();
                restServer.close();
            }
        }
        scanner.close();
    }
}
