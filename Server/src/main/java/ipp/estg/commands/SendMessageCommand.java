package ipp.estg.commands;

import ipp.estg.database.models.Channel;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelMessageRepository;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.database.repositories.interfaces.IUserMessageRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

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

        Channel channel = channelRepository.getById(channelIdInt);
        // Check if channel exists
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
        channelMessageRepository.sendMessage(channelIdInt, userIdInt, message);
        workerThread.sendMessage("Message sent successfully");

    }

    private void sendUserMessage(String senderId, String receiverId, String message) throws CannotWritetoFileException {
        int senderIdInt = Integer.parseInt(senderId);
        int receiverIdInt = Integer.parseInt(receiverId);

        // Check if sender exists
        if (userRepository.getById(senderIdInt) == null) {
            workerThread.sendMessage("ERROR: Sender does not exist");
            return;
        }

        // Check if receiver exists
        if (userRepository.getById(receiverIdInt) == null) {
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
}
