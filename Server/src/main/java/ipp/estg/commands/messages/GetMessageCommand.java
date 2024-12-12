package ipp.estg.commands.messages;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Channel;
import ipp.estg.database.models.ChannelMessage;
import ipp.estg.database.models.User;
import ipp.estg.database.models.UserMessage;
import ipp.estg.database.repositories.interfaces.IChannelMessageRepository;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.database.repositories.interfaces.IUserMessageRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.List;

/**
 * Command to retrieve messages from a channel or user and send them to the requesting user.
 */
public class GetMessageCommand implements ICommand {

    /**
     * Logger for logging events and errors.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(GetMessageCommand.class);

    /**
     * Worker thread responsible for handling the user request.
     */
    private final WorkerThread workerThread;

    /**
     * Worker thread responsible for handling the user request.
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
     * Flag indicating whether the command is related to a channel or a user.
     */
    private final boolean isChannel;

    /**
     * Constructor for the GetMessageCommand class.
     *
     * @param workerThread The worker thread handling the user request.
     * @param userRepository Repository for accessing user data.
     * @param channelRepository Repository for accessing channel data.
     * @param channelMessageRepository Repository for accessing channel messages.
     * @param userMessageRepository Repository for accessing user-to-user messages.
     * @param inputArray The input array containing the command arguments.
     * @param isChannel Flag indicating whether to retrieve channel messages or user messages.
     */
    public GetMessageCommand(WorkerThread workerThread, IUserRepository userRepository, IChannelRepository channelRepository, IChannelMessageRepository channelMessageRepository, IUserMessageRepository userMessageRepository, String[] inputArray, boolean isChannel) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.channelMessageRepository = channelMessageRepository;
        this.userMessageRepository = userMessageRepository;
        this.inputArray = inputArray;
        this.isChannel = isChannel;
    }

    /**
     * Sends the messages from the specified channel to the user.
     *
     * @param userId The ID of the user requesting the messages.
     * @param channelId The ID of the channel to retrieve messages from.
     */
    private void sendChannelMessages(int userId, String channelId) {
        int channelIdInt = Integer.parseInt(channelId);

        // Verify if channel exists
        Channel channel = channelRepository.getById(channelIdInt);
        if (channel == null) {
            workerThread.sendMessage("ERROR: Channel does not exist");
            LOGGER.error("Channel does not exist");
            return;
        }

        // Verify if user exists
        User user = userRepository.getById(userId);
        if (user == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            LOGGER.error("User with id " + userId + " does not exist");
            return;
        }

        // Verify if user is in the channel
        if(!channel.isUserInChannel(user.getId())) {
            workerThread.sendMessage("ERROR: User is not in the channel");
            LOGGER.error("User with id " + userId + " is not in the channel with id " + channelId);
            return;

        }

        // Parse and send messages
        List<ChannelMessage> messages = channelMessageRepository.getMessages(channelIdInt);
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(messages);
        workerThread.sendMessage(json);

        LOGGER.info("Messages sent to user with id " + userId + " from channel with id " + channelId);
    }

    /**
     * Sends the messages from a specific user to the requesting user.
     *
     * @param currentUserId The ID of the current user (requesting user).
     * @param fromUserId The ID of the user whose messages are being retrieved.
     */
    private void sendUserMessages(int currentUserId, String fromUserId) {
        int fromUserIdInt = Integer.parseInt(fromUserId);

        User currentUser = userRepository.getById(currentUserId);
        if (currentUser == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            LOGGER.error("User with id " + currentUserId + " does not exist");
            return;
        }
        User fromUser = userRepository.getById(fromUserIdInt);
        if(fromUser == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            LOGGER.error("User with id " + fromUserIdInt + " does not exist");
            return;
        }

        // Parse and send messages
        List<UserMessage> messages = userMessageRepository.getMessages(currentUserId, fromUserIdInt);
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(messages);
        workerThread.sendMessage(json);

        LOGGER.info("Messages sent to user with id " + fromUserIdInt);
    }

    /**
     * Executes the command based on the input. Depending on whether the command is for a channel or a user,
     * it retrieves the appropriate messages and sends them to the requesting user.
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
                String channelIdStr = inputArray[1];
                sendChannelMessages(senderId, channelIdStr);
            } else {
                String receiverIdStr = inputArray[1];
                sendUserMessages(senderId, receiverIdStr);
            }

        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get messages");
            LOGGER.error("Could not get messages", e);
        }
    }
}

