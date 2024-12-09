package ipp.estg.models;


public class Notification {
    private int id;
    private final int userId;
    private final String message;
    private final String notificationDate;


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
