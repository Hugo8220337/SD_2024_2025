package ipp.estg.database.models;

import java.io.Serializable;

public class Notification implements Serializable {
    private int id;
    private String notificationDate;
    private String userEmail;
    private String message;


    public Notification(int id, String notificationDate, String userEmail, String message) {
        this.id = id;
        this.userEmail = userEmail;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
