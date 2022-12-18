package multiplayer.minesweeper.cli;

import multiplayer.minesweeper.server.HttpServer;
import multiplayer.minesweeper.socket.SocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ListenActions implements Runnable {
    private boolean running = true;
    @Override
    public void run() {
        System.out.println("Starting terminal listener");
        while (running) {
            // Enter data using BufferReader
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));

            // Reading data using readLine
            String name = null;
            try {
                name = reader.readLine();
            } catch (IOException e) {
                System.out.println("Error occured while reading line");
                continue;
            }

            // Handle line
            if (name.equals("EXIT")) {
                System.out.println("Closing the service");
                running = false;
                HttpServer.getInstance().close();
                SocketServer.getInstance().close();
            }
        }
    }
}
