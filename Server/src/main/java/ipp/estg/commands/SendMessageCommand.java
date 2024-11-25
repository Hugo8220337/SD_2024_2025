package ipp.estg.commands;

import ipp.estg.constants.Addresses;
import ipp.estg.database.models.Channel;
import ipp.estg.database.models.ChannelMessage;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelMessageRepository;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.database.repositories.interfaces.IUserMessageRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.JsonConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SendMessageCommand implements ICommand {
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IChannelRepository channelRepository;
    private final IChannelMessageRepository channelMessageRepository;
    private final IUserMessageRepository userMessageRepository;
    private final String[] inputArray;

    /**
     * Indica se o comando é para um canal ou para um utilizador
     */
    private final boolean isChannel;

    public SendMessageCommand(WorkerThread workerThread, IUserRepository userRepository, IChannelRepository channelRepository, IChannelMessageRepository channelMessageRepository, IUserMessageRepository userMessageRepository, String[] inputArray, boolean isChannel) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.userMessageRepository = userMessageRepository;
        this.channelMessageRepository = channelMessageRepository;
        this.inputArray = inputArray;
        this.isChannel = isChannel;
    }

    private void sendChannelMessage(String channelId, String userId, String message) throws CannotWritetoFileException {
        int channelIdInt = Integer.parseInt(channelId);
        int userIdInt = Integer.parseInt(userId);

        // Check if channel exists
        Channel channel = channelRepository.getById(channelIdInt);
        if (channel == null) {
            workerThread.sendMessage("ERROR: Channel does not exist");
            return;
        }

        // Check if user exists
        User user = userRepository.getById(userIdInt);
        if (user == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            return;
        }

        // Check if user is a member of the channel
        if(!channel.isUserInChannel(user.getId())){
            workerThread.sendMessage("ERROR: User is not a member of the channel");
            return;
        }

        // Send message
        ChannelMessage newMessage = channelMessageRepository.sendMessage(channelIdInt, userIdInt, message);
        workerThread.sendMessage("Message sent successfully");

        // Send notification to channel
        sendChannelMessageNotification(channel.getPort(), newMessage);
    }

    private void sendUserMessage(String senderId, String receiverId, String message) throws CannotWritetoFileException {
        int senderIdInt = Integer.parseInt(senderId);
        int receiverIdInt = Integer.parseInt(receiverId);

        // Check if sender exists
        User sender = userRepository.getById(senderIdInt);
        if (sender == null) {
            workerThread.sendMessage("ERROR: Sender does not exist");
            return;
        }

        // Check if receiver exists
        User receiver = userRepository.getById(receiverIdInt);
        if (receiver == null) {
            workerThread.sendMessage("ERROR: Receiver does not exist");
            return;
        }

        // Send message
        userMessageRepository.sendMessage(senderIdInt, receiverIdInt, message);
        workerThread.sendMessage("Message sent successfully");
    }

    @Override
    public void execute() {
        try {
            if (isChannel) {
                String channelId = inputArray[1];
                String userId = inputArray[2];
                String message = inputArray[3];
                sendChannelMessage(channelId, userId, message);
            } else {
                String senderId = inputArray[1];
                String receiverId = inputArray[2];
                String message = inputArray[3];
                sendUserMessage(senderId, receiverId, message);
            }
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: " + e.getMessage());
            throw new RuntimeException("Could not send message", e);
        }
    }

    /**
     * Sends a message to a channel to notify that a message was sent
     * @param port
     */
    private void sendChannelMessageNotification(int port, ChannelMessage newMessage) {
        JsonConverter converter = new JsonConverter();
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(Addresses.CHANNEL_ADDRESS);

            // Convert message to json
            String json = converter.toJson(newMessage);

            byte[] buf = json.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException("Could not send message", e); // TODO alterar
        }
    }
}
