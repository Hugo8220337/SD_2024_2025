package ipp.estg.threads;

import ipp.estg.pages.chats.privateChat.PrivateChatPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PrivateMessageThread implements Runnable {
    private final PrivateChatPage privateChatPage;
    private final String host;
    private final int port;

    public PrivateMessageThread(PrivateChatPage privateChatPage, String host, int port) {
        this.privateChatPage = privateChatPage;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening for private messages on port " + port);

            while (privateChatPage.isRunning()) {
                // Wait for incoming connections
                Socket clientSocket = serverSocket.accept();

                // Handle the connection in a separate thread or process it inline
                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    String receivedMessage;
                    while ((receivedMessage = in.readLine()) != null) {
                        // Add the received message to the private chat page
                        privateChatPage.addMessageToList(receivedMessage, false);
                        System.out.println("Received private message: " + receivedMessage);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading message: " + e.getMessage());
                } finally {
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Error in PrivateMessageThread: " + e.getMessage());
        }
    }
}
