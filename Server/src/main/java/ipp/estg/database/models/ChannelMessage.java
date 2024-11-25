package ipp.estg.database.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChannelMessage implements Serializable {
    private int id;
    private int channelId;
    private int senderId;
    private String content;
    private LocalDateTime timestamp;

    public ChannelMessage(int id, int channelId, int senderId, String content) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
