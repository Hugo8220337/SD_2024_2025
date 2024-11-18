package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.Notification;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;


public interface INotificationRepository extends IRepository<Notification> {
    boolean add(
            int userId,
            String notificationDate,
            String message
    ) throws CannotWritetoFileException;

}
