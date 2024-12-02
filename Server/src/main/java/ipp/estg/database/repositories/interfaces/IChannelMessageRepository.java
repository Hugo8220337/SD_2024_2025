package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.ChannelMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IChannelMessageRepository extends IRepository<ChannelMessage> {
    ChannelMessage sendMessage(int channelId, int senderId, String message) throws CannotWritetoFileException;

    List<ChannelMessage> getMessages(int channelId);
}
