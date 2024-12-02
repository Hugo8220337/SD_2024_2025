package ipp.estg.services;

import ipp.estg.constants.DatabaseFiles;
import ipp.estg.database.models.User;
import ipp.estg.database.models.UserMessage;
import ipp.estg.database.repositories.ChannelMessageRepository;
import ipp.estg.database.repositories.ChannelRepository;
import ipp.estg.database.repositories.UserMessageRepository;
import ipp.estg.database.repositories.UserRepository;
import ipp.estg.database.repositories.interfaces.*;

public class StatisticsService {
    private final IUserRepository userRepository;
    private final IUserMessageRepository userMessageRepository;
    private final IChannelRepository channelRepository;
    private final IChannelMessageRepository channelMessageRepository;

    public StatisticsService() {
        this.userRepository = new UserRepository(DatabaseFiles.USERS_FILE);
        this.userMessageRepository = new UserMessageRepository(DatabaseFiles.USER_MESSAGES_FILE);
        this.channelRepository = new ChannelRepository(DatabaseFiles.CHANNELS_FILE);
        this.channelMessageRepository = new ChannelMessageRepository(DatabaseFiles.CHANNEL_MESSAGES_FILE);
    }

    public int getNumberOfUsers() {
        return userRepository.getAll().size();
    }

    public int getNumberOfUserMessages() {
        return userMessageRepository.getAll().size();
    }

    public int getNumberOfChannels() {
        return channelRepository.getAll().size();
    }

    public int getNumberOfChannelMessages() {
        return channelMessageRepository.getAll().size();
    }

    public int getNumberOfMessages() {
        return getNumberOfUserMessages() + getNumberOfChannelMessages();
    }

    public int getNumberOfMessagesFromUser(int userId) {
        return userMessageRepository.getAll().stream()
                .filter(userMessage -> userMessage.getSenderId() == userId)
                .toArray().length;
    }

    public int getNumberOfMessagesToUser(int userId) {
        return userMessageRepository.getAll().stream()
                .filter(userMessage -> userMessage.getReceiverId() == userId)
                .toArray().length;
    }

    public int getNumberOfMessagesFromChannel(int channelId) {
        return channelMessageRepository.getAll().stream()
                .filter(channelMessage -> channelMessage.getChannelId() == channelId)
                .toArray().length;
    }
}
