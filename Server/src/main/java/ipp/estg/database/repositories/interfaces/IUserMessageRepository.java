package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.UserMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

/**
 * Interface for the UserMessageRepository which extends the generic IRepository interface.
 */
public interface IUserMessageRepository extends IRepository<UserMessage> {

    /**
     * Sends a message from one user to another.
     *
     * @param senderId   The ID of the user sending the message.
     * @param receiverId The ID of the user receiving the message.
     * @param message    The message to be sent.
     * @return True if the message is sent successfully, otherwise false.
     * @throws CannotWritetoFileException if there is an error while writing to the file or database.
     */
    boolean sendMessage(int senderId, int receiverId, String message) throws CannotWritetoFileException;

    /**
     * Retrieves a list of messages between two users.
     *
     * @param fromUserId The ID of the user sending the message.
     * @param toUserId   The ID of the user receiving the message.
     * @return A list of messages between the two users.
     */
    List<UserMessage> getMessages(int fromUserId, int toUserId);
}
