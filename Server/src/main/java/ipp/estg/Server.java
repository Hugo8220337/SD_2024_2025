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
 * Servidor, responsável por aceitar conexões de clientes.
 */
public class Server extends Thread {
    private static final AppLogger LOGGER = AppLogger.getLogger(Server.class);
    /**
     * Multicast Sockets
     */
    private final MulticastSocket broadcastSocket; // Todos os clientes precisam de ter uma thread ligada neste socket


    private final ServerSocket serverSocket;

    /**
     * Lista de clientes, guarda-se o ip do cliente e a thread que trata desse cliente
     *
     * <p>
     *     Lista de threads que tratam de cada cliente
     *     Esta lista é sincronizada para evitar problemas de concorrência
     * </p>
     */
    private final ConcurrentHashMap<String, WorkerThread> connectedClients = new ConcurrentHashMap<>();

    private boolean running = true;

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


    public synchronized void removeClientFromList(WorkerThread workerThread) {
        connectedClients.remove(workerThread.getClientSocket().getInetAddress().getHostAddress());
    }

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

    public boolean isRunning() {
        return running;
    }

    public String getIpByUserId(int userId) {
        for (WorkerThread workerThread : connectedClients.values()) {
            if (workerThread.getCurrentUserId() == userId) {
                return workerThread.getClientSocket().getInetAddress().getHostAddress();
            }
        }
        return null;
    }
}
