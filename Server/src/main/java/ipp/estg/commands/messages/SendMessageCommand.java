package ipp.estg.commands.messages;

import ipp.estg.commands.ICommand;
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
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import static ipp.estg.constants.Addresses.PRIVATE_CHAT_ADDRESS;

public class SendMessageCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(SendMessageCommand.class);
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IChannelRepository channelRepository;
    private final IChannelMessageRepository channelMessageRepository;
    private final IUserMessageRepository userMessageRepository;
    private final String[] inputArray;

    /**
     * Indicates whether the command is for a channel or a user
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

    private void sendChannelMessage(String channelId, String userId, String message) throws CannotWritetoFileException, IOException {
        int channelIdInt = Integer.parseInt(channelId);
        int userIdInt = Integer.parseInt(userId);

        // Check if channel exists
        Channel channel = channelRepository.getById(channelIdInt);
        if (channel == null) {
            workerThread.sendMessage("ERROR: Channel does not exist");
            LOGGER.error("Channel " + channelIdInt + " does not exist");
            return;
        }

        // Check if user exists
        User user = userRepository.getById(userIdInt);
        if (user == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            LOGGER.error("User " + userIdInt + " does not exist");
            return;
        }

        // Check if user is a member of the channel
        if (!channel.isUserInChannel(user.getId())) {
            workerThread.sendMessage("ERROR: User is not a member of the channel");
            LOGGER.error("User " + userIdInt + " is not a member of the channel " + channelIdInt);
            return;
        }

        // Send message
        ChannelMessage newMessage = channelMessageRepository.sendMessage(channelIdInt, userIdInt, message);
        workerThread.sendMessage("SUCCESS: Message sent successfully");
        LOGGER.info("Message sent to channel " + channelIdInt + " by user " + userIdInt);

        // Send notification to channel
        sendChannelMessageNotification(channel.getPort(), newMessage);
    }

    private void sendUserMessage(String senderId, String receiverId, String message) throws CannotWritetoFileException, IOException {
        int senderIdInt = Integer.parseInt(senderId);
        int receiverIdInt = Integer.parseInt(receiverId);

        // Check if sender exists
        User sender = userRepository.getById(senderIdInt);
        if (sender == null) {
            workerThread.sendMessage("ERROR: Sender does not exist");
            LOGGER.error("Sender with id " + senderIdInt + " does not exist");
            return;
        }

        // Check if receiver exists
        User receiver = userRepository.getById(receiverIdInt);
        if (receiver == null) {
            workerThread.sendMessage("ERROR: Receiver does not exist");
            LOGGER.error("Receiver with id " + receiverIdInt + " does not exist");
            return;
        }

        // Send message
        userMessageRepository.sendMessage(senderIdInt, receiverIdInt, message);
        sendPrivateMessageNotification(message, receiver);
        workerThread.sendMessage("SUCCESS: Message sent successfully");


        LOGGER.info("Message sent from user " + senderIdInt + " to user " + receiverIdInt);
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
            LOGGER.error("Error sending message", e);
        }
    }

    /**
     * Sends a message to a channel to notify that a message was sent
     */
    private void sendChannelMessageNotification(int port, ChannelMessage newMessage) throws IOException {
        JsonConverter converter = new JsonConverter();
        MulticastSocket socket = new MulticastSocket();
        InetAddress group = InetAddress.getByName(Addresses.CHANNEL_ADDRESS);

        // Convert message to json
        String json = converter.toJson(newMessage);

        byte[] buf = json.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
        socket.send(packet);
        LOGGER.info("Message notification sent to channel " + port);

    }

    private void sendPrivateMessageNotification(String message, User receiver) {
        new Thread(() -> {
            try {
                Socket socket;
                socket = new Socket(PRIVATE_CHAT_ADDRESS, receiver.getPrivateMessagePort());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message); // send message to the receiver
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
