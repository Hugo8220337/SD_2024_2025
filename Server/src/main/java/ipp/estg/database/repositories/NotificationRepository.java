package ipp.estg.database.repositories;

import ipp.estg.database.models.Notification;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

/**
 * Repository for managing notification records. This repository allows adding, retrieving, and removing notifications.
 * It uses the {@link FileUtils} class for reading and writing data from a file and interacts with the {@link IUserRepository}
 * for user data.
 */
public class NotificationRepository implements INotificationRepository {

    /**
     * File utility for handling notification data.
     */
    private final FileUtils<Notification> fileUtils;

    /**
     * User repository to retrieve user data.
     */
    private final IUserRepository userRepository;

    /**
     * Constructor that initializes the repository with a specified file path and a user repository.
     *
     * @param filePath      The file path to read/write the notification data.
     * @param userRepository The user repository to access user data.
     */
    public NotificationRepository(String filePath, IUserRepository userRepository) {
        this.fileUtils = new FileUtils<>(filePath);
        this.userRepository = userRepository;
    }

    /**
     * Adds a new notification for a specific user.
     *
     * @param userId  The ID of the user to whom the notification is addressed.
     * @param message The message content of the notification.
     * @return True if the notification was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized boolean add(
            int userId,
            String message
    ) throws CannotWritetoFileException {
        List<Notification> notifications = fileUtils.readObjectListFromFile();

        Notification newNotification = new Notification(notifications.size() + 1, userId, message);
        notifications.add(newNotification);

        return fileUtils.writeObjectListToFile(notifications);
    }

    /**
     * Adds a new notification to all users.
     *
     * @param message The message content of the notification.
     * @return True if the notifications were successfully added to all users, otherwise false.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    public boolean addToAllUsers(String message) throws CannotWritetoFileException {
        List<Notification> notifications = fileUtils.readObjectListFromFile();
        List<User> users = userRepository.getAll();

        for(User user : users) {
            Notification newNotification = new Notification(notifications.size() + 1, user.getId(), message);
            notifications.add(newNotification);

        }

        return fileUtils.writeObjectListToFile(notifications);
    }

    /**
     * Retrieves all notifications for a specific user.
     *
     * @param userId The ID of the user whose notifications are to be retrieved.
     * @return A list of notifications for the specified user.
     */
    @Override
    public synchronized List<Notification> getAllByUserId(int userId) {
        List<Notification> notifications = fileUtils.readObjectListFromFile();
        notifications.removeIf(notification -> notification.getUserId() != userId);
        return notifications;
    }

    /**
     * Removes a notification by its ID.
     *
     * @param id The ID of the notification to remove.
     * @throws CannotWritetoFileException If there is an error while writing to the file.
     */
    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<Notification> notifications = fileUtils.readObjectListFromFile();
        notifications.removeIf(notification -> notification.getId() == id);

        fileUtils.writeObjectListToFile(notifications);
    }

    /**
     * Retrieves a notification by its ID.
     *
     * @param id The ID of the notification to retrieve.
     * @return The notification with the specified ID, or null if not found.
     */
    @Override
    public synchronized Notification getById(int id) {
        List<Notification> notifications = fileUtils.readObjectListFromFile();
        for (Notification notification : notifications) {
            if (notification.getId() == id) {
                return notification;
            }
        }
        return null;
    }

    /**
     * Retrieves all notifications from the repository.
     *
     * @return A list of all notifications.
     */
    @Override
    public synchronized List<Notification> getAll() {
        return fileUtils.readObjectListFromFile();
    }
}
