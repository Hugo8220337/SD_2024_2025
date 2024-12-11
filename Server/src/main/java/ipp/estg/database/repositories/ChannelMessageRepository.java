package ipp.estg.database.repositories;

import ipp.estg.database.models.ChannelMessage;
import ipp.estg.database.models.UserMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IChannelMessageRepository;
import ipp.estg.utils.FileUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelMessageRepository implements IChannelMessageRepository {
    private final FileUtils<ChannelMessage> fileUtils;

    public ChannelMessageRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    @Override
    public synchronized ChannelMessage sendMessage(int channelId, int senderId, String message) throws CannotWritetoFileException {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();

        ChannelMessage channelMessage = new ChannelMessage(userMessages.size() + 1, channelId, senderId, message);
        userMessages.add(channelMessage);

        fileUtils.writeObjectListToFile(userMessages);

        return channelMessage;
    }

    @Override
    public synchronized List<ChannelMessage> getMessages(int channelId) {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();

        // Get all messages form channel, and sort them by timestamp
        return userMessages.stream()
                .filter(msg -> msg.getChannelId() == channelId).sorted(Comparator.comparing(ChannelMessage::getTimestamp)).collect(Collectors.toList());
    }

    @Override
    public synchronized ChannelMessage getById(int id) {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();
        for(ChannelMessage userMessage : userMessages) {
            if(userMessage.getId() == id) {
                return userMessage;
            }
        }
        return null;
    }

    @Override
    public synchronized List<ChannelMessage> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<ChannelMessage> userMessages = fileUtils.readObjectListFromFile();
        userMessages.removeIf(userMessage -> userMessage.getId() == id);
        fileUtils.writeObjectListToFile(userMessages);
    }
}
