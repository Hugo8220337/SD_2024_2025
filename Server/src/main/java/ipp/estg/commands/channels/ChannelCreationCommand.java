package ipp.estg.commands.channels;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Channel;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class ChannelCreationCommand implements ICommand {
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IChannelRepository channelRepository;
    private final String[] inputArray;
    private static final AppLogger LOGGER = AppLogger.getLogger(ChannelCreationCommand.class);

    /**
     * True if the channel is being removed, false if the channel is being created
     */
    private final boolean remove;

    public ChannelCreationCommand(WorkerThread workerThread, IUserRepository userRepository, IChannelRepository channelRepository, String[] inputArray, boolean remove) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.inputArray = inputArray;
        this.remove = remove;
    }

    private void createChannel(int userId, String channelName) throws CannotWritetoFileException {
        // Check if channel name is empty
        if(channelName == null || channelName.isEmpty()) {
            workerThread.sendMessage("ERROR: Channel name cannot be empty");
            LOGGER.error("Channel name cannot be empty");
            return;

        }

        // Check if user exists
        User user = userRepository.getById(userId);
        if (user == null) {
            workerThread.sendMessage("ERROR: User not found");
            LOGGER.error("User with id " + userId + " not found");
            return;
        }

        // Check if user has permission to create channels
        if(!user.canCreateChannels()) {
            workerThread.sendMessage("ERROR: User does not have permission to create channels");
            LOGGER.error("User with id " + userId + " does not have permission to create channels");
            return;
        }

        channelRepository.add(userId, channelName);
        workerThread.sendMessage("SUCCESS: Channel created");
        LOGGER.info("Channel created with name " + channelName + " by user with id " + userId);
    }

    private void removeChannel(int userId, int channelId) throws CannotWritetoFileException {
        // Check if user exists
        User user = userRepository.getById(userId);
        if (user == null) {
            workerThread.sendMessage("ERROR: User not found");
            LOGGER.error("User with id " + userId + " not found");
            return;
        }

        // Check if channel exists
        Channel channel = channelRepository.getById(channelId);
        if (channel == null) {
            workerThread.sendMessage("ERROR: Channel not found");
            LOGGER.error("Channel with id " + channelId + " not found");
            return;
        }

        // Check if user has permission to remove channels
        if(channel.getOwnerId() != userId && !user.canDeleteSomeoneElseChannel()) {
            workerThread.sendMessage("ERROR: User does not have permission to remove this channel");
            LOGGER.error("User with id " + userId + " does not have permission to remove channel with id " + channelId);
            return;
        }

        channelRepository.remove(userId);
        workerThread.sendMessage("SUCCESS: Channel removed");
        LOGGER.info("Channel with id " + channelId + " removed by user with id " + userId);
    }

    @Override
    public void execute() {
        int userId = Integer.parseInt(inputArray[1]);

        try {
            if (remove) {
                int channelId = Integer.parseInt(inputArray[2]);
                removeChannel(userId, channelId);
            } else {
                String channelName = inputArray[2];
                createChannel(userId, channelName);
            }
        } catch (Exception e) {
            LOGGER.error("Error Creating/Removing Channel: " + e.getMessage());
            throw new RuntimeException("Error Creating/Removing Channel: " + e.getMessage());// TODO retirar porque o server não pode parar, substituir por um log
        }
    }

}
