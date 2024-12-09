package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.Notification;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;


public interface INotificationRepository extends IRepository<Notification> {
    boolean add(
            int userId,
            String message
    ) throws CannotWritetoFileException;

    List<Notification> getAllByUserId(int userId);
}
