package ipp.estg.commands.channels;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Channel;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class ChannelParticipationCommand implements ICommand {
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IChannelRepository channelRepository;
    private final String[] inputArray;
    private static final AppLogger LOGGER = AppLogger.getLogger(ChannelCreationCommand.class);

    /**
     * True if the user is joining the channel, false if the user is leaving the channel
     */
    private final boolean isLeaving;

    public ChannelParticipationCommand(WorkerThread workerThread, IUserRepository userRepository, ipp.estg.database.repositories.interfaces.IChannelRepository channelRepository, String[] inputArray, boolean isLeaving) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.inputArray = inputArray;
        this.isLeaving = isLeaving;
    }

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
