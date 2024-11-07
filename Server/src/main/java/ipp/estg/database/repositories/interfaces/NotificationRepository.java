package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.Notification;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface NotificationRepository {
    boolean addNotification(Notification notification) throws CannotWritetoFileException;

    boolean removeNotification(int id) throws CannotWritetoFileException;

    List<Notification> getAllNotifications();
}
