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


    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", notificationDate='" + notificationDate + '\'' +
                '}';
    }
}
