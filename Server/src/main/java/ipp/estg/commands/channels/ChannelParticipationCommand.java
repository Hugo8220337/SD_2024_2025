package ipp.estg.commands.channels;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Channel;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

public class ChannelParticipationCommand implements ICommand {
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IChannelRepository channelRepository;
    private final String[] inputArray;

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
            return;
        }

        Channel channel = channelRepository.getById(channelId);
        if(channel == null) {
            workerThread.sendMessage("ERROR: Channel not found");
            return;
        }

        channelRepository.addParticipant(channelId, userId);
    }

    private void removeParticipant(int channelId, int userId) throws CannotWritetoFileException {
        // Check if the user exists
        User user = userRepository.getById(userId);
        if (user == null) {
            workerThread.sendMessage("ERROR: User not found");
            return;
        }

        // Check if the channel exists
        Channel channel = channelRepository.getById(channelId);
        if(channel == null) {
            workerThread.sendMessage("ERROR: Channel not found");
            return;
        }

        // Channel is deleted when owner leaves
        if(user.getId() == channel.getOwnerId()) {
            channelRepository.remove(channelId);
            return;
        }

        channelRepository.removeParticipant(channelId, userId);
    }

    @Override
    public void execute() {
        int userId = Integer.parseInt(inputArray[1]);
        int channelId = Integer.parseInt(inputArray[2]);

        try {
            if (isLeaving) {
                removeParticipant(userId, channelId);
            } else {
                addParticipant(userId, channelId);
            }

            workerThread.sendMessage("SUCCESS");
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Could not write to file");
            throw new RuntimeException("Error adding/removing participant", e);
        }

    }
}
