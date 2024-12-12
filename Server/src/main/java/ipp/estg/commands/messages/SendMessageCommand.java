package ipp.estg.commands.messages;

import ipp.estg.Server;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

import static ipp.estg.constants.Addresses.PRIVATE_CHAT_PORT;


/**
 * Command that handles sending messages to channels or users.
 */
public class SendMessageCommand implements ICommand {

    /**
     * Logger for logging events and errors.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(SendMessageCommand.class);

    /**
     * Server instance to access server data.
     */
    private final Server server;

    /**
     * Worker thread responsible for handling the user request.
     */
    private final WorkerThread workerThread;

    /**
     * Repository to access user data.
     */
    private final IUserRepository userRepository;

    /**
     * Repository to access channel data.
     */
    private final IChannelRepository channelRepository;

    /**
     * Repository to access messages in channels.
     */
    private final IChannelMessageRepository channelMessageRepository;

    /**
     * Repository to access messages between users.
     */
    private final IUserMessageRepository userMessageRepository;

    /**
     * Input data passed to the command (arguments).
     */
    private final String[] inputArray;

    /**
     * Indicates whether the command is for a channel or a user
     */
    private final boolean isChannel;

    /**
     * Constructor for the SendMessageCommand.
     *
     * @param server               The server instance.
     * @param workerThread         The worker thread handling the connection.
     * @param userRepository       The user repository to fetch user data.
     * @param channelRepository    The channel repository to fetch channel data.
     * @param channelMessageRepository The channel message repository to handle channel messages.
     * @param userMessageRepository    The user message repository to handle user messages.
     * @param inputArray           The input parameters containing message details.
     * @param isChannel            Whether the message is being sent to a channel (true) or a user (false).
     */
    public SendMessageCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IChannelRepository channelRepository, IChannelMessageRepository channelMessageRepository, IUserMessageRepository userMessageRepository, String[] inputArray, boolean isChannel) {
        this.server =  server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.userMessageRepository = userMessageRepository;
        this.channelMessageRepository = channelMessageRepository;
        this.inputArray = inputArray;
        this.isChannel = isChannel;
    }

    /**
     * Sends a message to a channel.
     *
     * @param channelId The ID of the channel.
     * @param userId    The ID of the user sending the message.
     * @param message   The message to send.
     * @throws CannotWritetoFileException If an error occurs when writing to the file.
     * @throws IOException                If an I/O error occurs.
     */
    private void sendChannelMessage(String channelId, int userId, String message) throws CannotWritetoFileException, IOException {
        int channelIdInt = Integer.parseInt(channelId);

        // Check if channel exists
        Channel channel = channelRepository.getById(channelIdInt);
        if (channel == null) {
            workerThread.sendMessage("ERROR: Channel does not exist");
            LOGGER.error("Channel " + channelIdInt + " does not exist");
            return;
        }

        // Check if user exists
        User user = userRepository.getById(userId);
        if (user == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            LOGGER.error("User " + userId + " does not exist");
            return;
        }

        // Check if user is a member of the channel
        if (!channel.isUserInChannel(user.getId())) {
            workerThread.sendMessage("ERROR: User is not a member of the channel");
            LOGGER.error("User " + userId + " is not a member of the channel " + channelIdInt);
            return;
        }

        // Send message
        ChannelMessage newMessage = channelMessageRepository.sendMessage(channelIdInt, userId, message);
        workerThread.sendMessage("SUCCESS: Message sent successfully");
        LOGGER.info("Message sent to channel " + channelIdInt + " by user " + userId);

        // Send notification to channel
        sendChannelMessageNotification(channel.getPort(), newMessage);
    }

    /**
     * Sends a message between users.
     *
     * @param senderId    The ID of the sender.
     * @param receiverId  The ID of the receiver.
     * @param message     The message to send.
     * @throws CannotWritetoFileException If an error occurs when writing to the file.
     */
    private void sendUserMessage(int senderId, String receiverId, String message) throws CannotWritetoFileException {
        int receiverIdInt = Integer.parseInt(receiverId);

        // Check if sender exists
        User sender = userRepository.getById(senderId);
        if (sender == null) {
            workerThread.sendMessage("ERROR: Sender does not exist");
            LOGGER.error("Sender with id " + senderId + " does not exist");
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
        userMessageRepository.sendMessage(senderId, receiverIdInt, message);
        sendPrivateMessageNotification(message, receiver);
        workerThread.sendMessage("SUCCESS: Message sent successfully");

        LOGGER.info("Message sent from user " + senderId + " to user " + receiverIdInt);
    }

    /**
     * Executes the send message command.
     * This method checks the sender's login status and routes the message accordingly (to a channel or a user).
     */
    @Override
    public void execute() {
        int senderId = workerThread.getCurrentUserId();
        if (senderId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        try {
            if (isChannel) {
                String channelId = inputArray[1];
                String message = inputArray[2];
                sendChannelMessage(channelId, senderId, message);
            } else {
                String receiverId = inputArray[1];
                String message = inputArray[2];
                sendUserMessage(senderId, receiverId, message);
            }
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: " + e.getMessage());
            LOGGER.error("Error sending message", e);
        }
    }

    /**
     * Sends a notification to all members of a channel when a new message is sent.
     *
     * @param port        The port of the channel.
     * @param newMessage  The new message that was sent to the channel.
     * @throws IOException If an I/O error occurs while sending the notification.
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

    /**
     * Sends a notification to a private user when a message is received.
     *
     * @param message   The message sent to the user.
     * @param receiver  The receiver user object.
     */
    private void sendPrivateMessageNotification(String message, User receiver) {
        new Thread(() -> {
            String receiverIpAddress = server.getIpByUserId(receiver.getId());

            if(receiverIpAddress == null) {
                LOGGER.error("Could not find receiver IP address");
                return;
            }

            if(message.startsWith("SUCCESS")) {
                return;
            }

            try {
                Socket socket = new Socket(receiverIpAddress, PRIVATE_CHAT_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message); // send message to the receiver
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
