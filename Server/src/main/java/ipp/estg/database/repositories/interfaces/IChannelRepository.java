package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.Channel;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IChannelRepository {
    boolean createChannel(Channel channel) throws CannotWritetoFileException;

    List<Channel> getChannels();

    boolean isPortOnline(int port);

    void addParticipant(int channelId, String userId) throws CannotWritetoFileException;
}
