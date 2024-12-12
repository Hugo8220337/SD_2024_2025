package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.ChannelMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

/**
 * Interface for the ChannelMessageRepository which extends the generic IRepository interface.
 */
public interface IChannelMessageRepository extends IRepository<ChannelMessage> {

    /**
     * Sends a message to a channel.
     *
     * @param channelId The ID of the channel.
     * @param senderId  The ID of the user sending the message.
     * @param message   The message to be sent.
     * @return The message that was sent.
     * @throws CannotWritetoFileException if there is an error while writing to the file or database.
     */
    ChannelMessage sendMessage(int channelId, int senderId, String message) throws CannotWritetoFileException;

    /**
     * Retrieves a list of messages in a channel.
     *
     * @param channelId The ID of the channel.
     * @return A list of messages in the channel.
     */
    List<ChannelMessage> getMessages(int channelId);
}
