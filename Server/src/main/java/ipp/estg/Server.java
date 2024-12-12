package ipp.estg;

import ipp.estg.constants.Addresses;
import ipp.estg.threads.ReportsThread;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Server class is responsible for accepting client connections and managing communication.
 * It listens for incoming client connections, spawns worker threads to handle each client,
 * and handles broadcasting messages to clients via multicast.
 */
public class Server extends Thread {

    /**
     * Logger for the Server class that logs messages to the console and a file.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(Server.class);

    /**
     * Multicast socket used to send broadcast messages to clients.
     */
    private final MulticastSocket broadcastSocket; // Todos os clientes precisam de ter uma thread ligada neste socket

    /**
     * Server socket used to accept incoming client connections.
     */
    private final ServerSocket serverSocket;

    /**
     * A concurrent map that holds the connected clients and their respective worker threads.
     * The key is the client's IP address, and the value is the worker thread handling that client.
     */
    private final ConcurrentHashMap<String, WorkerThread> connectedClients = new ConcurrentHashMap<>();

    /**
     * Flag indicating whether the server is running.
     */
    private boolean running = true;

    /**
     * Constructs a new server instance. Initializes the server socket and multicast socket.
     * Starts a thread to periodically send reports.
     *
     * @throws IOException if an I/O error occurs while initializing the server
     */
    public Server() throws IOException {
        super("Server");
        this.serverSocket = new ServerSocket(Addresses.SERVER_PORT);
        this.broadcastSocket = new MulticastSocket(Addresses.MULTICAST_PORT);

        /**
         * Thread that sends reports periodically
         */
        Thread reportsThread = new Thread(new ReportsThread(this));
        reportsThread.start();
    }

    /**
     * Sends a broadcast message to all clients using multicast.
     *
     * @param message the message to send to all clients
     */
    public void sendBrodcastMessage(String message) {
        try {
            InetAddress group = InetAddress.getByName(Addresses.BROADCAST_ADDRESS);

            // send message
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Addresses.MULTICAST_PORT);
            broadcastSocket.send(packet);

            LOGGER.info("Broadcasted message: " + message.replace("\n", "").trim().strip());  // remove new lines
        } catch (IOException e) {
            LOGGER.error("Error while broadcasting message: " + e.getMessage());
        }
    }

    /**
     * Removes a client from the list of connected clients.
     * This is called when the client's worker thread terminates.
     *
     * @param workerThread the worker thread that is handling the client
     */
    public synchronized void removeClientFromList(WorkerThread workerThread) {
        connectedClients.remove(workerThread.getClientSocket().getInetAddress().getHostAddress());
    }

    /**
     * The main method that runs the server. It listens for incoming client connections,
     * accepts new connections, creates worker threads to handle them, and logs the events.
     */
    @Override
    public void run() {
        // Create logs directory if does not exist
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }

        LOGGER.info("Server started");
        try {
            try {
                while (running) {
                    Socket newClient = serverSocket.accept();

                    WorkerThread workerThread = new WorkerThread(this, newClient);
                    connectedClients.put(newClient.getInetAddress().getHostAddress(), workerThread);
                    workerThread.start();

                    LOGGER.info("New client Connected");
                }
            } finally {
                serverSocket.close();
                LOGGER.info("Server Closed");
            }
        } catch (Exception e) {
            LOGGER.error("Error while running the server: " + e.getMessage());
        }
    }

    /**
     * Returns whether the server is still running.
     *
     * @return true if the server is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Retrieves the IP address of a client by its user ID.
     *
     * @param userId the user ID of the client
     * @return the IP address of the client, or null if not found
     */
    public String getIpByUserId(int userId) {
        for (WorkerThread workerThread : connectedClients.values()) {
            if (workerThread.getCurrentUserId() == userId) {
                return workerThread.getClientSocket().getInetAddress().getHostAddress();
            }
        }
        return null;
    }
}
