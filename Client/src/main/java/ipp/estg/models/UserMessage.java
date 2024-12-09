package ipp.estg.models;

import java.time.LocalDateTime;

public class UserMessage {
    private int id;
    private final int senderId;
    private final int receiverId;
    private final String content;
    private final String timestamp;

    public UserMessage(int id, int senderId, int receiverId, String content, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
