package ipp.estg.models;

import java.time.LocalDateTime;

public class ChannelMessage {
    private int id;
    private final int channelId;
    private final int senderId;
    private final String content;
    private final String timestamp;

    public ChannelMessage(int id, int channelId, int senderId, String content, String timestamp) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }
}
