package multiplayer.minesweeper.cli;

import multiplayer.minesweeper.rest.client.HTTPClient;
import multiplayer.minesweeper.rest.server.HTTPServer;
import multiplayer.minesweeper.socket.SocketServer;

import java.util.Scanner;

public class CLIActions implements Runnable {
    private final HTTPServer restServer;
    private final HTTPClient restClient;
    private boolean running = true;

    public CLIActions(HTTPServer restServer, HTTPClient restClient) {
        this.restServer = restServer;
        this.restClient = restClient;
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
                SocketServer.get().close();
                restServer.close();
            }
        }
        scanner.close();
    }
}
