package ipp.estg.database.models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a message sent by a user to another user.
 * This class stores the details of the message including the sender, receiver, content, and timestamp.
 */
public class UserMessage implements Serializable {

    /**
     * The unique identifier for the message.
     */
    private final int id;

    /**
     * The ID of the user that sent the message.
     */
    private final int senderId;

    /**
     * The ID of the user that received the message.
     */
    private final int receiverId;

    /**
     * The content of the message.
     */
    private final String content;

    /**
     * The timestamp when the message was sent.
     */
    private final String timestamp;

    /**
     * Constructs a new UserMessage object with the specified details.
     * The timestamp is automatically set to the current time.
     *
     * @param id        The unique identifier for the message.
     * @param senderId  The unique identifier for the sender of the message.
     * @param receiverId The unique identifier for the receiver of the message.
     * @param content   The content of the message.
     */
    public UserMessage(int id, int senderId, int receiverId, String content) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now().toString();
    }

    /**
     * Returns the unique identifier for the message.
     *
     * @return The unique identifier for the message.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the unique identifier for the sender of the message.
     *
     * @return The unique identifier for the sender of the message.
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * Returns the unique identifier for the receiver of the message.
     *
     * @return The unique identifier for the receiver of the message.
     */
    public int getReceiverId() {
        return receiverId;
    }

    /**
     * Returns the content of the message.
     *
     * @return The content of the message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the timestamp of when the message was created.
     *
     * @return The timestamp of the message.
     */
    public String getTimestamp() {
        return timestamp;
    }

}
