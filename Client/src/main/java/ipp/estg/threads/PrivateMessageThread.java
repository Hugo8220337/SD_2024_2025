package ipp.estg.threads;

import ipp.estg.pages.chats.privateChat.PrivateChatPage;

public class PrivateMessageThread implements Runnable {

    private final PrivateChatPage privateChatPage;

    public PrivateMessageThread(PrivateChatPage privateChatPage) {
        this.privateChatPage = privateChatPage;
    }

    @Override
    public void run() {
        while (privateChatPage.isRunning()) {

        }
    }
}
