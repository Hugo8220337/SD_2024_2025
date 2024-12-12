package ipp.estg.database.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the model for a channel.
 * It contains the information related to the channel, including the owner, name, port, and participants.
 * The model supports two constructors:
 * <ul>
 *     <li>One for creating a channel with the owner, name, port, and participants.</li>
 *     <li>Another for creating a channel with the owner, name, and port, where the participants are set to an empty list.</li>
 * </ul>
 */
public class Channel implements Serializable {

    /**
     * The unique identifier for the channel.
     */
    private final int id;

    /**
     * The ID of the user who created the channel.
     */
    private final int ownerId;

    /**
     * The name of the channel.
     */
    private String name;

    /**
     * The port associated with the channel.
     */
    private int port;

    /**
     * The list of participants in the channel.
     */
    private final List<Integer> participants; // Participants ids

    /**
     * Constructor to initialize the Channel object with the owner, name, port, and participants.
     *
     * @param id the unique identifier of the channel.
     * @param ownerId the ID of the user who created the channel.
     * @param name the name of the channel.
     * @param port the port associated with the channel.
     * @param participants the list of participants in the channel.
     */
    public Channel(int id, int ownerId, String name, int port, List<Integer> participants) {
        this.id = id;
        this.ownerId = ownerId;
        this.port = port;
        this.name = name;
        this.participants = participants;
    }

    /**
     * Constructor to initialize the Channel object with the owner, name, and port.
     * The participants list is set to an empty list.
     *
     * @param id the unique identifier of the channel.
     * @param ownerId the ID of the user who created the channel.
     * @param name the name of the channel.
     * @param port the port associated with the channel.
     */
    public Channel(int id, int ownerId, String name, int port) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.port = port;
        this.participants = new ArrayList<>();
    }

    /**
     * Gets the unique identifier for the channel.
     *
     * @return the ID of the channel.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID of the user who created the channel.
     *
     * @return the ID of the channel owner.
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Gets the port associated with the channel.
     *
     * @return the port of the channel.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the name of the channel.
     *
     * @return the name of the channel.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of participants in the channel.
     *
     * @return the list of participants' user IDs.
     */
    public List<Integer> getParticipants() {
        return participants;
    }

    /**
     * Adds a participant to the channel.
     * The participant is added if they are not already in the channel.
     *
     * @param userId the user ID of the participant to be added.
     */
    public void addParticipant(int userId) {
        if (!participants.contains(userId)) {
            participants.add(userId);
        }
    }

    /**
     * Removes a participant from the channel.
     * The participant is removed based on their user ID.
     *
     * @param userId the user ID of the participant to be removed.
     */
    public void removeParticipant(int userId) {
        participants.removeIf(participant -> participant == userId);
    }

    /**
     * Checks if a user is a participant in the channel.
     *
     * @param userId the user ID to check.
     * @return true if the user is a participant in the channel, false otherwise.
     */
    public boolean isUserInChannel(int userId) {
        return participants.contains(userId);
    }
}
