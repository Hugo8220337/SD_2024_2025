package ipp.estg.database.repositories;

import ipp.estg.database.models.UserMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IUserMessageRepository;
import ipp.estg.utils.FileUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing user messages. This repository allows sending, retrieving, and removing messages.
 * It uses the {@link FileUtils} class for reading and writing message data from/to a file.
 */
public class UserMessageRepository implements IUserMessageRepository {

    /**
     * File utility for handling user message data.
     */
    private final FileUtils<UserMessage> fileUtils;

    /**
     * Constructor that initializes the repository with a specified file path.
     *
     * @param filePath The file path to read/write the user message data.
     */
    public UserMessageRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    /**
     * Sends a message from a sender to a receiver.
     *
     * @param senderId   The ID of the user sending the message.
     * @param receiverId The ID of the user receiving the message.
     * @param message    The message content.
     * @return True if the message was successfully sent, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean sendMessage(int senderId, int receiverId, String message) throws CannotWritetoFileException {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();

        UserMessage userMessage = new UserMessage(userMessages.size() + 1, senderId, receiverId, message);
        userMessages.add(userMessage);

        return fileUtils.writeObjectListToFile(userMessages);
    }

    /**
     * Retrieves all messages exchanged between two users.
     * This method considers messages in both directions (sender to receiver and receiver to sender).
     *
     * @param fromUserId The ID of the user sending the message.
     * @param toUserId   The ID of the user receiving the message.
     * @return A list of messages exchanged between the two users, sorted by timestamp.
     */
    @Override
    public synchronized List<UserMessage> getMessages(int fromUserId, int toUserId) {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();

        // Get all messages from user1 to user2 and vice-versa
        List<UserMessage> userMessages1 = userMessages.stream()
                .filter(userMessage -> userMessage.getSenderId() == fromUserId && userMessage.getReceiverId() == toUserId)
                .collect(Collectors.toList());
        List<UserMessage> userMessages2 = userMessages.stream()
                .filter(userMessage -> userMessage.getSenderId() == toUserId && userMessage.getReceiverId() == fromUserId)
                .toList();
        userMessages1.addAll(userMessages2);

        // sort messages by timestamp
        userMessages1.sort(Comparator.comparing(UserMessage::getTimestamp));

        return userMessages1;
    }

    /**
     * Retrieves a user message by its ID.
     *
     * @param id The ID of the message to retrieve.
     * @return The user message with the specified ID, or null if not found.
     */
    @Override
    public synchronized UserMessage getById(int id) {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();
        for(UserMessage userMessage : userMessages) {
            if(userMessage.getId() == id) {
                return userMessage;
            }
        }
        return null;
    }

    /**
     * Retrieves all user messages in the repository.
     *
     * @return A list of all user messages.
     */
    @Override
    public synchronized List<UserMessage> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    /**
     * Removes a user message by its ID.
     *
     * @param id The ID of the message to remove.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();
        userMessages.removeIf(userMessage -> userMessage.getId() == id);
        fileUtils.writeObjectListToFile(userMessages);
    }
}
