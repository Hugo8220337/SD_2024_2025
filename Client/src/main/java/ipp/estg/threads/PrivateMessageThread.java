package ipp.estg.threads;

import ipp.estg.Client;
import ipp.estg.pages.chats.privateChat.PrivateChatPage;
import ipp.estg.utils.AppLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class PrivateMessageThread implements Runnable {
    private static final AppLogger LOGGER = AppLogger.getLogger(PrivateMessageThread.class);

    private final PrivateChatPage privateChatPage;
    private final Client client;

    public PrivateMessageThread(PrivateChatPage privateChatPage, Client client) {
        this.privateChatPage = privateChatPage;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            Socket socket = this.client.getUnicastSocket();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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