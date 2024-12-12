package ipp.estg.database.models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This class represents a message sent in a channel.
 * It contains information about the message, including the channel ID, sender ID, content, and timestamp.
 */
public class ChannelMessage implements Serializable {

    /**
     * The unique identifier for the message.
     */
    private int id;

    /**
     * The unique identifier for the channel to which the message belongs.
     */
    private int channelId;

    /**
     * The unique identifier for the sender of the message.
     */
    private int senderId;

    /**
     * The content of the message.
     */
    private String content;

    /**
     * The timestamp when the message was sent.
     */
    private String timestamp;

    /**
     * Constructor to initialize a ChannelMessage object with the given parameters.
     * The timestamp is automatically set to the current date and time when the message is created.
     *
     * @param id the unique identifier for the message.
     * @param channelId the unique identifier for the channel where the message was sent.
     * @param senderId the unique identifier for the sender of the message.
     * @param content the content of the message.
     */
    public ChannelMessage(int id, int channelId, int senderId, String content) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = LocalDateTime.now().toString();
    }

    /**
     * Gets the unique identifier for the message.
     *
     * @return the ID of the message.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the unique identifier for the channel to which the message belongs.
     *
     * @return the channel ID.
     */
    public int getChannelId() {
        return channelId;
    }

    /**
     * Gets the unique identifier for the sender of the message.
     *
     * @return the sender ID.
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * Gets the content of the message.
     *
     * @return the content of the message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the timestamp when the message was sent.
     *
     * @return the timestamp of the message.
     */
    public String getTimestamp() {
        return timestamp;
    }
}
