package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.Notification;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

/**
 * Interface for the NotificationRepository which extends the generic IRepository interface.
 */
public interface INotificationRepository extends IRepository<Notification> {

    /**
     * Adds a new notification for a specific user.
     *
     * @param userId  The ID of the user.
     * @param message The message of the notification.
     * @return True if the notification is added successfully, otherwise false.
     * @throws CannotWritetoFileException if there is an error while writing to the file or database.
     */
    boolean add(
            int userId,
            String message
    ) throws CannotWritetoFileException;

    /**
     * Adds a new notification for all users.
     *
     * @param message The message of the notification.
     * @return True if the notification is added successfully, otherwise false.
     * @throws CannotWritetoFileException if there is an error while writing to the file or database.
     */
    boolean addToAllUsers(
            String message
    ) throws CannotWritetoFileException;

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of notifications for the user.
     */
    List<Notification> getAllByUserId(int userId);
}
