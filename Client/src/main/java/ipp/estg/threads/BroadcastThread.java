package ipp.estg.threads;

import ipp.estg.Client;
import ipp.estg.Main;
import ipp.estg.utils.AppLogger;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class BroadcastThread extends Thread {
    private static final AppLogger LOGGER = AppLogger.getLogger(BroadcastThread.class);

    private final Client client;
    private MulticastSocket broadcastSocket;

    public BroadcastThread(Client client, String boradcastAddress, int port) {
        super("broadcastThread");
        this.client = client;

        try {
            InetAddress group = InetAddress.getByName(boradcastAddress);
            this.broadcastSocket = new MulticastSocket(port);
            this.broadcastSocket.joinGroup(group);

            LOGGER.info("Broadcast thread started");
        } catch (UnknownHostException e) {
            LOGGER.error("Error while  joining the broadcast group: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error while creating the broadcast socket: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void displayBroadcastMessage(String message) {
        // aviso inicial
        JOptionPane.showMessageDialog(null,
                message,
                "Aviso Broadcast",
                JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void run() {
        try {
            while (client.isRunning()) {
                // receive message from server
                byte[] buffer = new byte[256]; // TODO arranjar uma maneira de saber o tamanho da mensagem
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                broadcastSocket.receive(packet);

                // Display broadcast message
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                displayBroadcastMessage(receivedMessage);

                LOGGER.info("Received broadcast message: " + receivedMessage.replace("\n", "").trim().strip()); // remove new lines
            }
        } catch (Exception e) {
            LOGGER.error("Error on broadcastThread run: " + e.getMessage());
        } finally {
            if (broadcastSocket != null) {
                broadcastSocket.close();
            }
        }
    }
}
