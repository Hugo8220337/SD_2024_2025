package ipp.estg.threads;

import ipp.estg.Client;
import ipp.estg.pages.chats.privateChat.PrivateChatPage;
import ipp.estg.utils.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PrivateMessageThread implements Runnable {
    private static final AppLogger LOGGER = AppLogger.getLogger(PrivateMessageThread.class);

    private final PrivateChatPage privateChatPage;

    private final BufferedReader in;

    public PrivateMessageThread(PrivateChatPage privateChatPage, Client client) {
        this.privateChatPage = privateChatPage;

        try {
            Socket socket = client.getUnicastSocket();
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
                if ((receivedMessage = in.readLine()) != null) {
                    privateChatPage.addMessageToList(receivedMessage, false);

                    LOGGER.info("Received private message: " + receivedMessage);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error in PrivateMessageThread: " + e.getMessage());
        }
    }
}