package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.UserMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IUserMessageRepository {
    boolean sendMessage(int senderId, int receiverId, String message) throws CannotWritetoFileException;

    List<UserMessage> getMessages(int userId);
}
