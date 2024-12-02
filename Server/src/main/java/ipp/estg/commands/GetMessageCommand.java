package ipp.estg.commands;

import ipp.estg.database.models.Channel;
import ipp.estg.database.models.ChannelMessage;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IChannelMessageRepository;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
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
    private final String[] inputArray;
    private static final AppLogger LOGGER = AppLogger.getLogger(GetMessageCommand.class);

    /**
     * Indica se o comando é para um canal ou para um utilizador
     */
    private final boolean isChannel;

    public GetMessageCommand(WorkerThread workerThread, IUserRepository userRepository, IChannelRepository channelRepository, IChannelMessageRepository channelMessageRepository, String[] inputArray, boolean isChannel) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.channelMessageRepository = channelMessageRepository;
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

    private void sendUserMessages(String userId) {
        int userIdInt = Integer.parseInt(userId);

        User user = userRepository.getById(userIdInt);
        if (user == null) {
            workerThread.sendMessage("ERROR: User does not exist");
            LOGGER.error("User with id " + userId + " does not exist");
            return;
        }

        // Parse and send messages
        List<ChannelMessage> messages = channelMessageRepository.getMessages(userIdInt);
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(messages);
        workerThread.sendMessage(json);
        LOGGER.info("Messages sent to user with id " + userId);
    }

    /**
     * TODO acho que falta um método para receber mensagens de um utilizador específico
     */

    @Override
    public void execute() {
        try {
            if (isChannel) {
                String channelIdStr = inputArray[1];
                String userIdStr = inputArray[2];
                sendChannelMessages(channelIdStr, userIdStr);
            } else {
                String senderIdStr = inputArray[1];
                sendUserMessages(senderIdStr);
            }

        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get messages");
            LOGGER.error("Could not get messages", e);
            throw new RuntimeException("Could not get messages", e); // TODO ver se é necessário
        }
    }
}

