package multiplayer.minesweeper.cli;

import multiplayer.minesweeper.rest.server.RestServer;
import multiplayer.minesweeper.socket.SocketServer;
import java.util.Scanner;

public class ListenActions implements Runnable {
    private boolean running = true;
    @Override
    public void run() {
        System.out.println("Starting terminal listener");
        Scanner scanner = new Scanner(System.in);
        while (running) {
            String name = scanner.nextLine();
            System.out.println(name);

            if (name.equals("EXIT")) {
                System.out.println("Closing the service");
                running = false;
                RestServer.getInstance().close();
                SocketServer.getInstance().close();
            }
        }
        scanner.close();
    }
}
