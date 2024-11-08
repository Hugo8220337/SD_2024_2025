package ipp.estg;

import ipp.estg.constants.Addresses;
import ipp.estg.constants.DatabaseFiles;
import ipp.estg.database.repositories.FileNotificationRepository;
import ipp.estg.database.repositories.FileUserRepository;
import ipp.estg.database.repositories.interfaces.NotificationRepository;
import ipp.estg.database.repositories.interfaces.UserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.SynchronizedArrayList;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Servidor, responsável por aceitar conexões de clientes.
 */
public class Server extends Thread {
    /**
     * Multicast Sockets
     */
    private MulticastSocket broadCastSocket; // Todos os clientes precisam de ter uma thread ligada neste socket


    private ServerSocket serverSocket;
    private List<WorkerThread> clientList = new SynchronizedArrayList<>();
    private boolean running = true;

    public Server() throws IOException {
        super("Server");
        this.serverSocket = new ServerSocket(Addresses.SERVER_PORT);
        this.broadCastSocket = new MulticastSocket(Addresses.MULTICAST_PORT);
    }

    public void sendBrodcastMessage(String message) {
        try {
            InetAddress group = InetAddress.getByName(Addresses.BROADCAST_ADDRESS);
            MulticastSocket broadcastSocket = new MulticastSocket(Addresses.MULTICAST_PORT);
            broadcastSocket.joinGroup(group);

            // send message
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Addresses.MULTICAST_PORT);
            broadcastSocket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException("Error while broadcasting message: " + e.getMessage());
        }
    }


    public synchronized void removeClientFromList(WorkerThread workerThread) {
        clientList.remove(workerThread);
    }

    @Override
    public void run() {
        try {
            try {
                while (running) {
                    Socket newClient = serverSocket.accept();
                    clientList.add(new WorkerThread(this, newClient));
                    clientList.get(clientList.size() - 1).start();
                }
            } finally {
                serverSocket.close();
            }
        } catch (Exception e) {
            System.out.println("Error while running the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
