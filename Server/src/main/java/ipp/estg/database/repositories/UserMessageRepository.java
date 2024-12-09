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
    public synchronized List<UserMessage> getMessages(int fromUserId, int toUserId) {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();

        return userMessages.stream()
                .filter(userMessage -> userMessage.getSenderId() == fromUserId && userMessage.getReceiverId() == toUserId)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized UserMessage getById(int id) {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();
        for(UserMessage userMessage : userMessages) {
            if(userMessage.getId() == id) {
                return userMessage;
            }
        }
        return null;
    }

    @Override
    public synchronized List<UserMessage> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<UserMessage> userMessages = fileUtils.readObjectListFromFile();
        userMessages.removeIf(userMessage -> userMessage.getId() == id);
        fileUtils.writeObjectListToFile(userMessages);
    }
}
