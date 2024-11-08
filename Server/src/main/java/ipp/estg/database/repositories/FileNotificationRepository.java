package ipp.estg.database.repositories;

import ipp.estg.database.models.Notification;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.NotificationRepository;
import ipp.estg.database.repositories.interfaces.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileNotificationRepository implements NotificationRepository {
    private final UserRepository userRepository;
    private final String filePath;

    public FileNotificationRepository(String filePath, UserRepository userRepository) {
        this.filePath = filePath;
        this.userRepository = userRepository;
    }

    @Override
    public synchronized boolean addNotification(
            int userId,
            String notificationDate,
            String message
    ) throws CannotWritetoFileException {
        List<Notification> notifications = readNotificationsFromFile();

        Notification newNotification = new Notification(notifications.size() + 1, userId, notificationDate, message);
        notifications.add(newNotification);

        return writeNotificationsToFile(notifications);
    }

    @Override
    public synchronized boolean removeNotification(int id) throws CannotWritetoFileException {
        List<Notification> notifications = readNotificationsFromFile();
        notifications.removeIf(notification -> notification.getId() == id);

        return writeNotificationsToFile(notifications);
    }

    @Override
    public synchronized List<Notification> getAllNotifications() {
        return readNotificationsFromFile();
    }

    private List<Notification> readNotificationsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<Notification>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>(); // Retorna lista vazia se não conseguir ler
        }
    }

    private boolean writeNotificationsToFile(List<Notification> notifications) throws CannotWritetoFileException {
        File file = new File(filePath);
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(notifications);
            return true;

        } catch (IOException e) {
            throw new CannotWritetoFileException("Cannot write to file", e.getMessage());
        }
    }
}
