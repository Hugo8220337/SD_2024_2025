package ipp.estg.commands.channels;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Channel;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

/**
 * Command for handling channel creation and removal operations.
 * This command allows a user to create or remove a channel based on the specified input.
 */
public class ChannelCreationCommand implements ICommand {

    /**
     * The worker thread handling the client connection.
     */
    private final WorkerThread workerThread;

    /**
     * The user repository for accessing user data.
     */
    private final IUserRepository userRepository;

    /**
     * The channel repository for accessing channel data.
     */
    private final IChannelRepository channelRepository;

    /**
     * The input data array from the client.
     */
    private final String[] inputArray;

    /**
     * The logger instance for the ChannelCreationCommand class.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(ChannelCreationCommand.class);

    /**
     * True if the channel is being removed, false if the channel is being created
     */
    private final boolean remove;

    /**
     * Constructs a new ChannelCreationCommand instance.
     *
     * @param workerThread      the worker thread handling the client connection
     * @param userRepository    the user repository for accessing user data
     * @param channelRepository the channel repository for accessing channel data
     * @param inputArray        the input data array from the client
     * @param remove            true if the operation is to remove a channel, false if to create a channel
     */
    public ChannelCreationCommand(WorkerThread workerThread, IUserRepository userRepository, IChannelRepository channelRepository, String[] inputArray, boolean remove) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.inputArray = inputArray;
        this.remove = remove;
    }

    /**
     * Creates a channel for the specified user.
     *
     * @param userId      the ID of the user creating the channel
     * @param channelName the name of the channel to be created
     * @throws CannotWritetoFileException if there is an error writing to the database
     */
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

    /**
     * Removes a channel for the specified user.
     *
     * @param userId    the ID of the user requesting the channel removal
     * @param channelId the ID of the channel to be removed
     * @throws CannotWritetoFileException if there is an error writing to the database
     */
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

        channelRepository.remove(channelId);
        workerThread.sendMessage("SUCCESS: Channel removed");
        LOGGER.info("Channel with id " + channelId + " removed by user with id " + userId);
    }

    /**
     * Executes the channel creation or removal operation based on the provided input.
     */
    @Override
    public void execute() {
        int userId = workerThread.getCurrentUserId();
        if (userId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        try {
            if (remove) {
                int channelId = Integer.parseInt(inputArray[1]);
                removeChannel(userId, channelId);
            } else {
                String channelName = inputArray[1];
                createChannel(userId, channelName);
            }
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not create/remove channel");
            LOGGER.error("Error Creating/Removing Channel: " + e.getMessage());
        }
    }

}
