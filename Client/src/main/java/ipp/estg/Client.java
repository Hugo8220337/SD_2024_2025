package ipp.estg;

import ipp.estg.constants.Addresses;
import ipp.estg.models.UserTypes;
import ipp.estg.threads.BroadcastThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    /**
     * User data
     */
    private String loggedUserId;
    private String authToken; // não está a ser usado de momento
    private UserTypes loggedUserType;

    /**
     * Multicast Sockets
     */
    private MulticastSocket broadcastSocket;

    /**
     * Threads
     */
    private Thread boradcastThread;

    private boolean isRunning = true;

    public Client() throws IOException {
        this.broadcastSocket = new MulticastSocket(Addresses.MULTICAST_PORT);

        // start threads
        this.boradcastThread = new BroadcastThread(this, Addresses.BROADCAST_ADDRESS, Addresses.MULTICAST_PORT);
        this.boradcastThread.start();
    }


    public String sendMessageToServer(String command) {
        Socket socket = null;
        BufferedReader in;
        PrintWriter out;

        try {
            socket = new Socket(Addresses.SERVER_ADDRESS, Addresses.SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            // Send message to server
            out.println(command);

            // Get response from server
            String response = in.readLine();
            System.out.println(response);
            return response;
        } catch (UnknownHostException e) {
            throw new RuntimeException("There are no servers avaiable for that address.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void stop() {
        this.isRunning = false;
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

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setLoggedUserType(UserTypes loggedUserType) {
        this.loggedUserType = loggedUserType;
    }

    public UserTypes getLoggedUserType() {
        return loggedUserType;
    }
}
