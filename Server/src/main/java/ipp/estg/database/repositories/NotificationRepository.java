package ipp.estg.database.repositories;

import ipp.estg.database.models.Notification;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

public class NotificationRepository implements INotificationRepository {
    private final FileUtils<Notification> fileUtils;
    private final IUserRepository userRepository;

    public NotificationRepository(String filePath, IUserRepository userRepository) {
        this.fileUtils = new FileUtils<>(filePath);
        this.userRepository = userRepository;
    }

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

    public boolean addToAllUsers(String message) throws CannotWritetoFileException {
        List<Notification> notifications = fileUtils.readObjectListFromFile();
        List<User> users = userRepository.getAll();

        for(User user : users) {
            Notification newNotification = new Notification(notifications.size() + 1, user.getId(), message);
            notifications.add(newNotification);

        }

        return fileUtils.writeObjectListToFile(notifications);
    }

    @Override
    public synchronized List<Notification> getAllByUserId(int userId) {
        List<Notification> notifications = fileUtils.readObjectListFromFile();
        notifications.removeIf(notification -> notification.getUserId() != userId);
        return notifications;
    }

    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<Notification> notifications = fileUtils.readObjectListFromFile();
        notifications.removeIf(notification -> notification.getId() == id);

        fileUtils.writeObjectListToFile(notifications);
    }

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

    @Override
    public synchronized List<Notification> getAll() {
        return fileUtils.readObjectListFromFile();
    }
}
