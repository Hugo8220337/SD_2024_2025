package ipp.estg;

import ipp.estg.constants.Addresses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    /**
     * Multicast Sockets
     */
    private MulticastSocket broadCastSocket;

    private boolean running = true;

    public Client() throws IOException {
        this.broadCastSocket = new MulticastSocket(Addresses.MULTICAST_PORT);
    }

    public boolean isClientRunning() {
        return running;
    }

    public void stopClient() {
        running = false;
    }

    public void sendMessageToServer(String command) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            socket = new Socket(Addresses.SERVER_ADDRESS, Addresses.SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            // Send message to server
            out.println(command);

            // Get response from server
            String response = in.readLine();
            System.out.println(response);
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
}
