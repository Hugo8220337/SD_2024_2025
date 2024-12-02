package ipp.estg.threads;

import ipp.estg.pages.chats.privateChat.PrivateChatPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
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
        try {
            Socket socket = new Socket(host, port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String receivedMessage;
            while (privateChatPage.isRunning()) {
                if ((receivedMessage = in.readLine()) != null) {
                    privateChatPage.addMessageToList(receivedMessage, false);
                    System.out.println("Received private message: " + receivedMessage);
                }
            }

        } catch (Exception e) {
            System.err.println("Error in PrivateMessageThread: " + e.getMessage());
            e.printStackTrace();
        }

    }
}