package ipp.estg.threads;

import ipp.estg.Client;
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

    private final ServerSocket myPrivateMessageServerSocket;

    public PrivateMessageThread(PrivateChatPage privateChatPage) {
        this.privateChatPage = privateChatPage;

        try {
            // Create a server socket to receive private messages
            this.myPrivateMessageServerSocket = new ServerSocket(Addresses.PRIVATE_CHAT_PORT);
        } catch (IOException e) {
            LOGGER.error("Error creating private message thread: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            String receivedMessage;
            while (privateChatPage.isRunning()) {
                LOGGER.debug("Waiting for private messages...");

                Socket socket = myPrivateMessageServerSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while ((receivedMessage = in.readLine()) != null && !receivedMessage.startsWith("SUCCESS:")) {
                    LOGGER.debug("Received private message: " + receivedMessage);

                    privateChatPage.addMessageToList(receivedMessage, false);

                    LOGGER.info("Received private message: " + receivedMessage);
                }

            }

            LOGGER.info("Private message thread stopped");
        } catch (Exception e) {
            LOGGER.error("Error in PrivateMessageThread: " + e.getMessage());
        }
    }
}