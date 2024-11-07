package ipp.estg.database.repositories;

import ipp.estg.database.models.Notification;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.NotificationRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileNotificationRepository implements NotificationRepository {
    private final String filePath;

    public FileNotificationRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean addNotification(Notification notification) throws CannotWritetoFileException {
        List<Notification> notifications = readNotificationsFromFile();
        notifications.add(notification);
        return writeNotificationsToFile(notifications);
    }

    @Override
    public boolean removeNotification(int id) throws CannotWritetoFileException {
        List<Notification> notifications = readNotificationsFromFile();
        notifications.removeIf(notification -> notification.getId() == id);

        return writeNotificationsToFile(notifications);
    }

    @Override
    public List<Notification> getAllNotifications() {
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
