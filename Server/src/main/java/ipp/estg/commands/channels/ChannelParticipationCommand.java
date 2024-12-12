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
 * Command to handle a user's participation in a channel.
 * This command allows a user to join or leave a channel.
 */
public class ChannelParticipationCommand implements ICommand {

    /**
     * The worker thread associated with the current command.
     */
    private final WorkerThread workerThread;

    /**
     * The repository for user-related operations.
     */
    private final IUserRepository userRepository;

    /**
     * The repository for channel-related operations.
     */
    private final IChannelRepository channelRepository;

    /**
     * The input array containing command parameters.
     */
    private final String[] inputArray;

    /**
     * The logger for the ChannelParticipationCommand class.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(ChannelCreationCommand.class);

    /**
     * True if the user is joining the channel, false if the user is leaving the channel
     */
    private final boolean isLeaving;

    /**
     * Constructs a new ChannelParticipationCommand.
     *
     * @param workerThread the worker thread associated with the current command
     * @param userRepository the repository for user-related operations
     * @param channelRepository the repository for channel-related operations
     * @param inputArray the input array containing command parameters
     * @param isLeaving flag indicating if the user is leaving the channel
     */
    public ChannelParticipationCommand(WorkerThread workerThread, IUserRepository userRepository, ipp.estg.database.repositories.interfaces.IChannelRepository channelRepository, String[] inputArray, boolean isLeaving) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.inputArray = inputArray;
        this.isLeaving = isLeaving;
    }

    /**
     * Adds a user to the specified channel.
     *
     * @param channelId the ID of the channel to add the user to
     * @param userId the ID of the user to add to the channel
     * @throws CannotWritetoFileException if there is an error writing to the file
     */
    private void addParticipant(int channelId, int userId) throws CannotWritetoFileException {
        User user = userRepository.getById(userId);
        if (user == null) {
            workerThread.sendMessage("ERROR: User not found");
            LOGGER.error("User with id " + userId + " not found");
            return;
        }

        Channel channel = channelRepository.getById(channelId);
        if(channel == null) {
            workerThread.sendMessage("ERROR: Channel not found");
            LOGGER.error("Channel with id " + channelId + " not found");
            return;
        }

        channelRepository.addParticipant(channelId, userId);
    }

    /**
     * Removes a user from the specified channel.
     * If the user is the owner of the channel, the channel will be removed entirely.
     *
     * @param channelId the ID of the channel to remove the user from
     * @param userId the ID of the user to remove from the channel
     * @throws CannotWritetoFileException if there is an error writing to the file
     */
    private void removeParticipant(int channelId, int userId) throws CannotWritetoFileException {
        // Check if the user exists
        User user = userRepository.getById(userId);
        if (user == null) {
            workerThread.sendMessage("ERROR: User not found");
            LOGGER.error("User with id " + userId + " not found");
            return;
        }

        // Check if the channel exists
        Channel channel = channelRepository.getById(channelId);
        if(channel == null) {
            workerThread.sendMessage("ERROR: Channel not found");
            LOGGER.error("Channel with id " + channelId + " not found");
            return;
        }

        // Channel is deleted when owner leaves
        if(user.getId() == channel.getOwnerId()) {
            channelRepository.remove(channelId);
            workerThread.sendMessage("SUCCESS: Channel removed");
            LOGGER.info("Channel with id " + channelId + " removed by user with id " + userId);
            return;
        }

        channelRepository.removeParticipant(channelId, userId);
        LOGGER.info("User with id " + userId + " removed from channel with id " + channelId);
    }

    /**
     * Executes the command to either add or remove a user from a channel.
     * Sends appropriate success or error messages based on the operation outcome.
     */
    @Override
    public void execute() {
        int userId = workerThread.getCurrentUserId();
        if (userId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        int channelId = Integer.parseInt(inputArray[1]);

        try {
            if (isLeaving) {
                removeParticipant(channelId, userId);
            } else {
                addParticipant(channelId, userId);
            }

            workerThread.sendMessage("SUCCESS");
            LOGGER.info("User with id " + userId + " added/removed from channel with id " + channelId);
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Could not write to file");
            LOGGER.error("Error adding/removing participant", e);
        }

    }
}
