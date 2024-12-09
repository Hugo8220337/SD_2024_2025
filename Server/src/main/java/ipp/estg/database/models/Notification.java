package ipp.estg.database.models;

import java.io.Serializable;
import java.time.LocalDate;

public class Notification implements Serializable {
    private int id;
    private final int userId;
    private final String message;
    private final String notificationDate;


    public Notification(int id, int userId, String message, String notificationDate) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.notificationDate = notificationDate;
    }

    public Notification(int id, int userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.notificationDate = LocalDate.now().toString();
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
