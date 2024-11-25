package ipp.estg.threads;

import ipp.estg.constants.Addresses;
import ipp.estg.models.ChannelMessage;
import ipp.estg.pages.chats.channelChat.ChannelChatPage;
import ipp.estg.utils.JsonConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ChannelThread implements Runnable {
    private final ChannelChatPage channelChatPage;
    private final int port;

    public ChannelThread(ChannelChatPage channelChatPage, int port) {
        this.channelChatPage = channelChatPage;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            MulticastSocket socket = new MulticastSocket(port);
            InetAddress group = InetAddress.getByName(Addresses.CHANNEL_ADDRESS);
            socket.joinGroup(group);

            byte[] buf = new byte[300];
            while (channelChatPage.isRunning()) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                // parse json
                JsonConverter converter = new JsonConverter();
                ChannelMessage message = converter.fromJsonToObject(received, ChannelMessage.class);

                // Add message to the list
                channelChatPage.addMessageToList(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
