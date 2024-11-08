package ipp.estg.database.models;

import java.io.Serializable;

public class Notification implements Serializable {
    private int id;
    private int userId;
    private String message;
    private String notificationDate;


    public Notification(int id, int userId, String notificationDate, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.notificationDate = notificationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
