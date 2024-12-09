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

public class GetMessageCommand implements ICommand {
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IChannelRepository channelRepository;
    private final IChannelMessageRepository channelMessageRepository;
    private final IUserMessageRepository userMessageRepository;
    private final String[] inputArray;
    private static final AppLogger LOGGER = AppLogger.getLogger(GetMessageCommand.class);

    /**
     * Indica se o comando é para um canal ou para um utilizador
     */
    private final boolean isChannel;

    public GetMessageCommand(WorkerThread workerThread, IUserRepository userRepository, IChannelRepository channelRepository, IChannelMessageRepository channelMessageRepository, IUserMessageRepository userMessageRepository, String[] inputArray, boolean isChannel) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.channelMessageRepository = channelMessageRepository;
        this.userMessageRepository = userMessageRepository;
        this.inputArray = inputArray;
        this.isChannel = isChannel;
    }

    private void sendChannelMessages(String channelId, String userId) {
        int channelIdInt = Integer.parseInt(channelId);

        // Verify if channel exists
        Channel channel = channelRepository.getById(channelIdInt);
        if (channel == null) {
            workerThread.sendMessage("ERROR: Channel does not exist");
            LOGGER.error("Channel does not exist");
            return;
        }

        // Verify if user exists
        User user = userRepository.getById(Integer.parseInt(userId));
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

    private void sendUserMessages(String currentUserId, String fromUserId) {
        int currentUserIdInt = Integer.parseInt(currentUserId);
        int fromUserIdInt = Integer.parseInt(fromUserId);

        User currentUser = userRepository.getById(currentUserIdInt);
        if (currentUser == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            LOGGER.error("User with id " + currentUserIdInt + " does not exist");
            return;
        }
        User fromUser = userRepository.getById(fromUserIdInt);
        if(fromUser == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            LOGGER.error("User with id " + fromUserIdInt + " does not exist");
            return;
        }

        // Parse and send messages
        List<UserMessage> messages = userMessageRepository.getMessages(currentUserIdInt, fromUserIdInt);
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(messages);
        workerThread.sendMessage(json);

        LOGGER.info("Messages sent to user with id " + fromUserIdInt);
    }

    @Override
    public void execute() {
        try {
            if (isChannel) {
                String channelIdStr = inputArray[1];
                String userIdStr = inputArray[2];
                sendChannelMessages(channelIdStr, userIdStr);
            } else {
                String senderIdStr = inputArray[1];
                String receiverIdStr = inputArray[2];
                sendUserMessages(senderIdStr, receiverIdStr);
            }

        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get messages");
            LOGGER.error("Could not get messages", e);
        }
    }
}

