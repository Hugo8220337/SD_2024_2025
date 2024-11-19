package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.Message;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IMessageRepository {
    boolean sendMessage(Message message) throws CannotWritetoFileException;

    List<Message> getMessages(String userId);
}
