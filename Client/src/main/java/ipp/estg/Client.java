package ipp.estg;

import ipp.estg.constants.Addresses;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.models.UserTypes;
import ipp.estg.threads.BroadcastThread;
import ipp.estg.threads.ReportThread;
import ipp.estg.utils.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final AppLogger LOGGER = AppLogger.getLogger(Client.class);

    /**
     * User data
     */
    private String loggedUserId;
    private UserTypes loggedUserType;

    /**
     * Socket to communicate with the server by broadcast
     */
    private MulticastSocket broadcastSocket;

    private Socket unicastSocket;
    private BufferedReader in;
    private PrintWriter out;

    private boolean isRunning = true;

    public Client() throws IOException {
        connectToServer();
        startThreads();
    }


    public String sendMessageToServer(String command) {
        try {
            out.println(command);
            String response = in.readLine();
            LOGGER.info("Received message from server: " + response);
            return response;
        } catch (IOException e) {
            LOGGER.error("Error while connecting to server: " + e.getMessage());
            try {
                reconnect();
                return sendMessageToServer(command);
            } catch (IOException reconnectionException) {
                throw new RuntimeException("Failed to reconnect to the server.", reconnectionException);
            }
        }
    }

    private void reconnect() throws IOException {
        LOGGER.info("Attempting to reconnect to the server...");
        closeConnections();
        connectToServer();
        LOGGER.info("Reconnected to the server.");
    }

    private void connectToServer() throws IOException {
        this.broadcastSocket = new MulticastSocket(Addresses.MULTICAST_PORT);
        this.unicastSocket = new Socket(Addresses.SERVER_ADDRESS, Addresses.SERVER_PORT);
        this.in = new BufferedReader(new InputStreamReader(unicastSocket.getInputStream()));
        this.out = new PrintWriter(unicastSocket.getOutputStream(), true);
    }

    private void startThreads() {
        Thread broadcastThread = new BroadcastThread(this, Addresses.BROADCAST_ADDRESS, Addresses.MULTICAST_PORT);
        Thread reportThread = new ReportThread(this, Addresses.REPORT_ADDRESS, Addresses.REPORT_PORT);
        broadcastThread.start();
        reportThread.start();
    }

    private void closeConnections() {
        try {
            if (unicastSocket != null && !unicastSocket.isClosed()) {
                unicastSocket.close();
            }
            if (broadcastSocket != null && !broadcastSocket.isClosed()) {
                broadcastSocket.close();
            }
        } catch (IOException e) {
            LOGGER.error("Error while closing sockets: " + e.getMessage());
        }
    }

    public void stop() {
        this.isRunning = false;
        closeConnections();
    }

    public boolean isRunning() {
        return isRunning;
    }


    public void setLoggedUserId(String loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    public String getLoggedUserId() {
        return loggedUserId;
    }

    public void setLoggedUserType(UserTypes loggedUserType) {
        this.loggedUserType = loggedUserType;
    }

    public UserTypes getLoggedUserType() {
        return loggedUserType;
    }

}
