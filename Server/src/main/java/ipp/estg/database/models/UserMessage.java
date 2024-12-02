package ipp.estg.database.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserMessage implements Serializable {
    private final int id;
    private final int senderId;
    private final int receiverId;
    private final String content;
    private final String timestamp;

    public UserMessage(int id, int senderId, int receiverId, String content) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now().toString();
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
