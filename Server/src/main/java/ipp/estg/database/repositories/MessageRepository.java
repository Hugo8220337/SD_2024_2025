package ipp.estg.database.repositories;

import ipp.estg.database.models.Message;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMessageRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MessageRepository implements IMessageRepository {
    private final FileUtils<Message> fileUtils;

    public MessageRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    @Override
    public synchronized boolean sendMessage(Message message) throws CannotWritetoFileException {
        List<Message> messages = fileUtils.readObjectListFromFile();
        messages.add(message);
        return fileUtils.writeObjectListToFile(messages);
    }

    @Override
    public List<Message> getMessages(String userId) {
        List<Message> messages = fileUtils.readObjectListFromFile();
        return messages.stream()
                .filter(msg -> msg.getReceiverId().equals(userId))
                .collect(Collectors.toList());
    }
}
