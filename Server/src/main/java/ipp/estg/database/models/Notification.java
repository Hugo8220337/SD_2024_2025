package ipp.estg.database.models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class represents a notification model.
 * It contains the notification details, including the user ID, message, and the date when the notification was created.
 */
public class Notification implements Serializable {

    /**
     * The unique identifier for the notification.
     */
    private int id;

    /**
     * The ID of the user who will receive the notification.
     */
    private final int userId;

    /**
     * The message associated with the notification.
     */
    private final String message;

    /**
     * The date when the notification was created.
     */
    private final String notificationDate;

    /**
     * Constructor to initialize a Notification object with the given parameters.
     *
     * @param id the unique identifier for the notification.
     * @param userId the ID of the user receiving the notification.
     * @param message the message content of the notification.
     * @param notificationDate the date the notification was created.
     */
    public Notification(int id, int userId, String message, String notificationDate) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.notificationDate = notificationDate;
    }

    /**
     * Constructor to initialize a Notification object with the given parameters.
     * The notification date is set to the current date.
     *
     * @param id the unique identifier for the notification.
     * @param userId the ID of the user receiving the notification.
     * @param message the message content of the notification.
     */
    public Notification(int id, int userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.notificationDate = LocalDate.now().toString();
    }

    /**
     * Gets the unique identifier for the notification.
     *
     * @return the ID of the notification.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the notification.
     *
     * @param id the ID to set for the notification.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the date when the notification was created.
     *
     * @return the notification creation date.
     */
    public String getNotificationDate() {
        return notificationDate;
    }

    /**
     * Gets the ID of the user receiving the notification.
     *
     * @return the ID of the user receiving the notification.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets the message content of the notification.
     *
     * @return the notification message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns a string representation of the Notification object.
     * The string contains the ID, user ID, message, and the notification date.
     *
     * @return a string representation of the notification.
     */
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
