package ipp.estg.database.repositories;

import ipp.estg.database.models.ChannelMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelMessageRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelMessageRepository implements IChannelMessageRepository {
    private final FileUtils<ChannelMessage> fileUtils;

    public ChannelMessageRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    @Override
    public synchronized boolean sendMessage(int channelId, int senderId, String message) throws CannotWritetoFileException {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();

        ChannelMessage channelMessage = new ChannelMessage(userMessages.size() + 1, channelId, senderId, message);
        userMessages.add(channelMessage);

        return fileUtils.writeObjectListToFile(userMessages);
    }

    @Override
    public synchronized List<ChannelMessage> getMessages(int channelId) {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();
        return userMessages.stream()
                .filter(msg -> msg.getChannelId() == channelId)
                .collect(Collectors.toList());
    }
}
