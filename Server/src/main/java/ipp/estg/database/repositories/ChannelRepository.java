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
    public synchronized boolean createChannel(Channel channel) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();
        channels.add(channel);
        return fileUtils.writeObjectListToFile(channels);
    }

    @Override
    public List<Channel> getChannels() {
        return fileUtils.readObjectListFromFile();
    }

    /**
     * See if the port is being used at the moment
     */
    public synchronized boolean isPortOnline(int port) {
        List<Channel> channels = getChannels();
        for(Channel channel : channels) {
            if(channel.isOpen() && channel.getPort() == port) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void addParticipant(int channelId, String userId) throws CannotWritetoFileException {
        List<Channel> channels = fileUtils.readObjectListFromFile();
        int userIdInt = Integer.parseInt(userId);

        for (Channel channel : channels) {
            if (channel.getId() == channelId) {
                channel.getParticipants().add(userIdInt);
                break;
            }
        }
        fileUtils.writeObjectListToFile(channels);
    }
}
