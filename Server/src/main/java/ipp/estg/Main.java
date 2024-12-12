package ipp.estg;

import java.io.IOException;

/**
 * Main class to launch the server application.
 * This class contains the entry point of the application and starts the server.
 */
public class Main {

    /**
     * The main method that serves as the entry point of the application.
     * It creates an instance of the {@link Server} class and starts the server.
     *
     * @param args command-line arguments passed to the application (not used in this case)
     * @throws IOException if an I/O error occurs during server startup
     */
    public static void main(String[] args) throws IOException {
        new Server().start();
    }
}