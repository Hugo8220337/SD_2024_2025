package ipp.estg.database.repositories;

import ipp.estg.database.models.Channel;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;

public class ChannelRepository implements IChannelRepository {
    private final FileUtils<Channel> fileUtils;

    public ChannelRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    @Override
    public synchronized boolean add(int ownerId, String channelName) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();

        int id = channels.size() + 1;
        int port = channels.size() + 1000;
        Channel newChannel = new Channel(id, ownerId, channelName, port);

        channels.add(newChannel);
        return fileUtils.writeObjectListToFile(channels);
    }


    /**
     * See if the port is being used at the moment
     */
    @Override
    public synchronized boolean isPortOnline(int port) {
        List<Channel> channels = getAll();
        for (Channel channel : channels) {
            if (channel.getPort() == port) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void addParticipant(int channelId, int userId) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();

        for (Channel channel : channels) {
            if (channel.getId() == channelId) {
                channel.addParticipant(userId);
                break;
            }
        }
        fileUtils.writeObjectListToFile(channels);
    }

    @Override
    public synchronized void removeParticipant(int channelId, int userId) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();

        for (Channel channel : channels) {
            if (channel.getId() == channelId) {
                channel.removeParticipant(userId);
                break;
            }
        }
        fileUtils.writeObjectListToFile(channels);
    }

    @Override
    public Channel getById(int id) {
        List<Channel> channels = fileUtils.readObjectListFromFile();
        for (Channel channel : channels) {
            if (channel.getId() == id) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    @Override
    public void remove(int id) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();
        channels.removeIf(channel -> channel.getId() == id);
        fileUtils.writeObjectListToFile(channels);
    }
}
