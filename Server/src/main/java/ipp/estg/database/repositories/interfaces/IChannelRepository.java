package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.Channel;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

/**
 * Interface for the ChannelRepository which extends the generic IRepository interface.
 * This interface provides methods for channel-specific operations such as adding, updating,
 * and managing participants within a channel.
 */
public interface IChannelRepository extends IRepository<Channel> {

    /**
     * Adds a new channel to the repository.
     *
     * @param ownerId     The ID of the user who owns the channel.
     * @param channelName The name of the channel.
     * @return True if the channel is successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file or database.
     */
    boolean add(int ownerId, String channelName) throws CannotWritetoFileException;

    /**
     * Checks if the port of the channel is online.
     *
     * @param port The port of the channel.
     * @return True if the port is online, otherwise false.
     */
    boolean isPortOnline(int port);

    /**
     * Adds a participant to a specified channel.
     *
     * @param channelId The ID of the channel.
     * @param userId    The ID of the user to add as a participant.
     * @throws CannotWritetoFileException If there is an error while writing to the file or database.
     */
    void addParticipant(int channelId, int userId) throws CannotWritetoFileException;

    /**
     * Removes a participant from a specified channel.
     *
     * @param channelId The ID of the channel.
     * @param userId    The ID of the user to remove as a participant.
     * @throws CannotWritetoFileException If there is an error while writing to the file or database.
     */
    void removeParticipant(int channelId, int userId) throws CannotWritetoFileException;
}
