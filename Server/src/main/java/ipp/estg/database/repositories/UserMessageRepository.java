package ipp.estg.database.repositories;

import ipp.estg.database.models.UserMessage;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IUserMessageRepository;
import ipp.estg.utils.FileUtils;

import java.util.List;
import java.util.stream.Collectors;

public class UserMessageRepository implements IUserMessageRepository {
    private final FileUtils<UserMessage> fileUtils;

    public UserMessageRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    @Override
    public synchronized boolean sendMessage(int senderId, int receiverId, String message) throws CannotWritetoFileException {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();

        UserMessage userMessage = new UserMessage(userMessages.size() + 1, senderId, receiverId, message);
        userMessages.add(userMessage);

        return fileUtils.writeObjectListToFile(userMessages);
    }

    @Override
    public List<UserMessage> getMessages(int userId) {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();
        return userMessages.stream()
                .filter(msg -> msg.getReceiverId() == userId)
                .collect(Collectors.toList());
    }
}
