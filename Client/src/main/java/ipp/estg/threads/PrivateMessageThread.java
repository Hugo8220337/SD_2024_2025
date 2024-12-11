package ipp.estg.threads;

import ipp.estg.constants.Addresses;
import ipp.estg.pages.chats.privateChat.PrivateChatPage;
import ipp.estg.utils.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PrivateMessageThread implements Runnable {
    private static final AppLogger LOGGER = AppLogger.getLogger(PrivateMessageThread.class);

    private final PrivateChatPage privateChatPage;
    private ServerSocket privateMessageserverSocket;


    public PrivateMessageThread(PrivateChatPage privateChatPage) {
        this.privateChatPage = privateChatPage;
    }

    public void closeServer() {
        if (privateMessageserverSocket != null && !privateMessageserverSocket.isClosed()) {
            try {
                privateMessageserverSocket.close();
                LOGGER.info("Server socket closed successfully.");
            } catch (IOException e) {
                LOGGER.error("Error closing server socket: " + e.getMessage());
            }
        }
    }


    @Override
    public void run() {
        try {
            privateMessageserverSocket = new ServerSocket(Addresses.PRIVATE_CHAT_PORT);
            privateMessageserverSocket.setReuseAddress(true); // Allow immediate reuse of the port
            LOGGER.info("Private message server started on port: " + Addresses.PRIVATE_CHAT_PORT);

            while (privateChatPage.isRunning()) {
                LOGGER.debug("Waiting for private messages...");
                Socket socket = privateMessageserverSocket.accept();  // Wait for a connection
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String receivedMessage;
                    while ((receivedMessage = in.readLine()) != null && !receivedMessage.startsWith("SUCCESS:")) {
                        LOGGER.debug("Received private message: " + receivedMessage);
                        privateChatPage.addMessageToList(receivedMessage, false);
                    }

                } catch (IOException e) {
                    LOGGER.info(e.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while starting private message server: " + e.getMessage());
        } finally {
            closeServer();
        }
    }
}