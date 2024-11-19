package ipp.estg.database.models;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;

    public Message(int id, String senderId, String receiverId, String content) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
