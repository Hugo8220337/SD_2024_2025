package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.Channel;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;


public interface IChannelRepository extends IRepository<Channel> {
    boolean add(int ownerId, String channelName) throws CannotWritetoFileException;

    boolean isPortOnline(int port);

    void addParticipant(int channelId, int userId) throws CannotWritetoFileException;

    void removeParticipant(int channelId, int userId) throws CannotWritetoFileException;
}
