package ipp.estg.database.repositories;

import ipp.estg.database.models.Channel;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

/**
 * Repository for managing Channels, including adding, updating,
 * retrieving, and removing channels.
 * This class utilizes the {@link FileUtils} class for file operations.
 */
public class ChannelRepository implements IChannelRepository {

    /**
     * File utility for handling Channel data.
     */
    private final FileUtils<Channel> fileUtils;

    /**
     * Constructor that initializes the repository with a specified file path.
     *
     * @param filePath The file path to read/write the channel data.
     */
    public ChannelRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    /**
     * Adds a new channel with the specified owner ID and channel name.
     *
     * @param ownerId     The ID of the owner of the channel.
     * @param channelName The name of the channel.
     * @return True if the channel was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean add(int ownerId, String channelName) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();

        int id = channels.size() + 1;
        int port = channels.size() + 1000;
        Channel newChannel = new Channel(id, ownerId, channelName, port);

        channels.add(newChannel);
        return fileUtils.writeObjectListToFile(channels);
    }

    /**
     * Retrieves all channels from the repository.
     *
     * See if the port is being used at the moment
     *
     * @return A list of all channels.
     */
    @Override
    public synchronized boolean isPortOnline(int port) {
        List<Channel> channels = getAll();
        for (Channel channel : channels) {
            if (channel.getPort() == port) {
                return false;
            }
        }

        return true;
    }

    /**
     * Adds a participant to a channel by adding the user ID to the list of participants.
     *
     * @param channelId The ID of the channel to add the participant to.
     * @param userId    The ID of the user to add as a participant.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void addParticipant(int channelId, int userId) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();

        for (Channel channel : channels) {
            if (channel.getId() == channelId) {
                channel.addParticipant(userId);
                break;
            }
        }
        fileUtils.writeObjectListToFile(channels);
    }

    /**
     * Removes a participant from a channel by removing the user ID from the list of participants.
     *
     * @param channelId The ID of the channel to remove the participant from.
     * @param userId    The ID of the user to remove as a participant.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void removeParticipant(int channelId, int userId) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();

        for (Channel channel : channels) {
            if (channel.getId() == channelId) {
                channel.removeParticipant(userId);
                break;
            }
        }
        fileUtils.writeObjectListToFile(channels);
    }

    /**
     * Retrieves a channel by its ID.
     *
     * @param id The unique identifier of the channel.
     * @return The {@link Channel} object with the specified ID, or null if not found.
     */
    @Override
    public synchronized Channel getById(int id) {
        List<Channel> channels = fileUtils.readObjectListFromFile();
        for (Channel channel : channels) {
            if (channel.getId() == id) {
                return channel;
            }
        }
        return null;
    }

    /**
     * Retrieves all channels from the repository.
     *
     * @return A list of all {@link Channel} objects in the repository.
     */
    @Override
    public synchronized List<Channel> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    /**
     * Removes a channel from the repository by its ID.
     *
     * @param id The ID of the channel to remove.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();
        channels.removeIf(channel -> channel.getId() == id);
        fileUtils.writeObjectListToFile(channels);
    }
}
