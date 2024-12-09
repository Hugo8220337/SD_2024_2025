package ipp.estg;

import ipp.estg.constants.Addresses;
import ipp.estg.models.UserTypes;
import ipp.estg.threads.BroadcastThread;
import ipp.estg.threads.ReportThread;
import ipp.estg.utils.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
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
    private int loggedUserPrivateMessagePort;

    /**
     * Socket to communicate with the server by broadcast
     */
    private MulticastSocket broadcastSocket;

    /**
     * Socket to communicate with the server by unicast
     */
    private Socket unicastSocket;

    /**
     * Threads
     */
    private Thread boradcastThread;
    private Thread reportThread;

    private boolean isRunning = true;

    public Client() throws IOException {
        this.broadcastSocket = new MulticastSocket(Addresses.MULTICAST_PORT);
        this.unicastSocket = new Socket(Addresses.SERVER_ADDRESS, Addresses.SERVER_PORT);

        // start threads
        this.boradcastThread = new BroadcastThread(this, Addresses.BROADCAST_ADDRESS, Addresses.MULTICAST_PORT);
        this.reportThread = new ReportThread(this, Addresses.REPORT_ADDRESS, Addresses.REPORT_PORT);
        this.boradcastThread.start();
        this.reportThread.start();

    }


    public String sendMessageToServer(String command) {
        BufferedReader in;
        PrintWriter out;

        try {
            out = new PrintWriter(unicastSocket.getOutputStream(), true);
            in = new BufferedReader(new java.io.InputStreamReader(unicastSocket.getInputStream()));

            // Send message to server
            out.println(command);

            // Get response from server
            String response = in.readLine();

            LOGGER.info("Received message from server: " + response);

            return response;
        } catch (UnknownHostException e) {
            LOGGER.error("Unknown host: " + e.getMessage());
            throw new RuntimeException("There are no servers avaiable for that address.");
        } catch (IOException e) {
            LOGGER.error("Error while connecting to server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setLoggedUserPrivateMessagePort(int loggedUserPrivateMessagePort) {
        this.loggedUserPrivateMessagePort = loggedUserPrivateMessagePort;
    }

    public int getLoggedUserPrivateMessagePort() {
        return loggedUserPrivateMessagePort;
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

    public Socket getUnicastSocket() {
        return unicastSocket;
    }
}
