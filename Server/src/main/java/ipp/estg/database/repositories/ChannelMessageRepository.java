package ipp.estg.database.repositories;

import ipp.estg.database.models.ChannelMessage;
import ipp.estg.database.models.UserMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelMessageRepository;
import ipp.estg.utils.FileUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing Channel Messages, including adding, updating,
 * retrieving, and removing messages.
 * This class utilizes the {@link FileUtils} class for file operations.
 */
public class ChannelMessageRepository implements IChannelMessageRepository {

    /**
     * File utility for handling Channel Messages data.
     */
    private final FileUtils<ChannelMessage> fileUtils;

    /**
     * Constructor that initializes the repository with a specified file path.
     *
     * @param filePath The file path to read/write the channel messages data.
     */
    public ChannelMessageRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    /**
     * Sends a message to a channel by creating a new {@link ChannelMessage} object
     * and adding it to the list of messages in the file.
     *
     * @param channelId The ID of the channel to send the message to.
     * @param senderId  The ID of the user sending the message.
     * @param message   The message to send.
     * @return The {@link ChannelMessage} that was sent.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized ChannelMessage sendMessage(int channelId, int senderId, String message) throws CannotWritetoFileException {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();

        ChannelMessage channelMessage = new ChannelMessage(userMessages.size() + 1, channelId, senderId, message);
        userMessages.add(channelMessage);

        fileUtils.writeObjectListToFile(userMessages);

        return channelMessage;
    }

    /**
     * Retrieves all messages from a specific channel, sorted by timestamp.
     *
     * @param channelId The ID of the channel to retrieve messages from.
     * @return A list of all {@link ChannelMessage} objects from the specified channel.
     */
    @Override
    public synchronized List<ChannelMessage> getMessages(int channelId) {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();

        // Get all messages form channel, and sort them by timestamp
        return userMessages.stream()
                .filter(msg -> msg.getChannelId() == channelId).sorted(Comparator.comparing(ChannelMessage::getTimestamp)).collect(Collectors.toList());
    }

    /**
     * Retrieves a message by its ID.
     *
     * @param id The ID of the message to retrieve.
     * @return The {@link ChannelMessage} with the specified ID, or null if not found.
     */
    @Override
    public synchronized ChannelMessage getById(int id) {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();
        for(ChannelMessage userMessage : userMessages) {
            if(userMessage.getId() == id) {
                return userMessage;
            }
        }
        return null;
    }

    /**
     * Retrieves all messages from all channels.
     *
     * @return A list of all {@link ChannelMessage} objects.
     */
    @Override
    public synchronized List<ChannelMessage> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    /**
     * Removes a message by its ID.
     *
     * @param id The ID of the message to remove.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();
        userMessages.removeIf(userMessage -> userMessage.getId() == id);
        fileUtils.writeObjectListToFile(userMessages);
    }
}
