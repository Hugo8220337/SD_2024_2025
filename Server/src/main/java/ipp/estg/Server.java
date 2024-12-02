package ipp.estg;

import ipp.estg.constants.Addresses;
import ipp.estg.threads.ReportsThread;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.SynchronizedArrayList;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Servidor, responsável por aceitar conexões de clientes.
 */
public class Server extends Thread {
    private static final AppLogger LOGGER = AppLogger.getLogger(Server.class);
    /**
     * Multicast Sockets
     */
    private MulticastSocket broadcastSocket; // Todos os clientes precisam de ter uma thread ligada neste socket


    private ServerSocket serverSocket;
    private List<WorkerThread> clientList = new SynchronizedArrayList<>();
    private boolean running = true;

    /**
     * Thread that sends reports periodically
     */
    private Thread reportsThread;

    public Server() throws IOException {
        super("Server");
        this.serverSocket = new ServerSocket(Addresses.SERVER_PORT);
        this.broadcastSocket = new MulticastSocket(Addresses.MULTICAST_PORT);

        // start reports thread
        this.reportsThread = new Thread(new ReportsThread(this));
        this.reportsThread.start();
    }

    public void sendBrodcastMessage(String message) {
        try {
            InetAddress group = InetAddress.getByName(Addresses.BROADCAST_ADDRESS);

            // send message
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Addresses.MULTICAST_PORT);
            broadcastSocket.send(packet);

            LOGGER.info("Broadcasted message: " + message);
        } catch (IOException e) {
            LOGGER.error("Error while broadcasting message: " + e.getMessage());
            throw new RuntimeException("Error while broadcasting message: " + e.getMessage());
        }
    }


    public synchronized void removeClientFromList(WorkerThread workerThread) {
        clientList.remove(workerThread);
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
                    clientList.add(new WorkerThread(this, newClient));
                    clientList.get(clientList.size() - 1).start();

                    LOGGER.info("New client connected");
                }
            } finally {
                serverSocket.close();
                LOGGER.info("Server closed");
            }
        } catch (Exception e) {
            System.out.println("Error while running the server: " + e.getMessage());
            LOGGER.error("Error while running the server: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }
}
