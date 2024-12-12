package ipp.estg.services;

import ipp.estg.constants.DatabaseFiles;
import ipp.estg.database.repositories.ChannelMessageRepository;
import ipp.estg.database.repositories.ChannelRepository;
import ipp.estg.database.repositories.UserMessageRepository;
import ipp.estg.database.repositories.UserRepository;
import ipp.estg.database.repositories.interfaces.*;


/**
 * Service class responsible for retrieving statistical information about the application.
 * It interacts with repositories to fetch data on users, messages, channels, and messages within channels.
 */
public class StatisticsService {

    /**
     * Repositories used to fetch data from the database.
     */
    private final IUserRepository userRepository;

    /**
     * Repositories used to fetch data from the database.
     */
    private final IUserMessageRepository userMessageRepository;

    /**
     * Repositories used to fetch data from the database.
     */
    private final IChannelRepository channelRepository;

    /**
     * Repositories used to fetch data from the database.
     */
    private final IChannelMessageRepository channelMessageRepository;

    /**
     * Constructs a StatisticsService object and initializes the repositories.
     * Uses predefined file paths to load data from the files.
     */
    public StatisticsService() {
        this.userRepository = new UserRepository(DatabaseFiles.USERS_FILE);
        this.userMessageRepository = new UserMessageRepository(DatabaseFiles.USER_MESSAGES_FILE);
        this.channelRepository = new ChannelRepository(DatabaseFiles.CHANNELS_FILE);
        this.channelMessageRepository = new ChannelMessageRepository(DatabaseFiles.CHANNEL_MESSAGES_FILE);
    }

    /**
     * Gets the total number of users in the system.
     *
     * @return The total number of users.
     */
    public int getNumberOfUsers() {
        return userRepository.getAll().size();
    }

    /**
     * Gets the total number of user messages in the system.
     *
     * @return The total number of user messages.
     */
    public int getNumberOfUserMessages() {
        return userMessageRepository.getAll().size();
    }

    /**
     * Gets the total number of channels in the system.
     *
     * @return The total number of channels.
     */
    public int getNumberOfChannels() {
        return channelRepository.getAll().size();
    }

    /**
     * Gets the total number of channel messages in the system.
     *
     * @return The total number of channel messages.
     */
    public int getNumberOfChannelMessages() {
        return channelMessageRepository.getAll().size();
    }

    /**
     * Gets the total number of messages in the system, including both user and channel messages.
     *
     * @return The total number of messages in the system.
     */
    public int getNumberOfMessages() {
        return getNumberOfUserMessages() + getNumberOfChannelMessages();
    }

    /**
     * Gets the total number of messages sent by a specific user.
     *
     * @param userId The ID of the user.
     * @return The number of messages sent by the user.
     */
    public int getNumberOfMessagesFromUser(int userId) {
        return userMessageRepository.getAll().stream()
                .filter(userMessage -> userMessage.getSenderId() == userId)
                .toArray().length;
    }

    /**
     * Gets the total number of messages received by a specific user.
     *
     * @param userId The ID of the user.
     * @return The number of messages received by the user.
     */
    public int getNumberOfMessagesToUser(int userId) {
        return userMessageRepository.getAll().stream()
                .filter(userMessage -> userMessage.getReceiverId() == userId)
                .toArray().length;
    }

    /**
     * Gets the total number of messages sent within a specific channel.
     *
     * @param channelId The ID of the channel.
     * @return The number of messages sent within the channel.
     */
    public int getNumberOfMessagesFromChannel(int channelId) {
        return channelMessageRepository.getAll().stream()
                .filter(channelMessage -> channelMessage.getChannelId() == channelId)
                .toArray().length;
    }
}
