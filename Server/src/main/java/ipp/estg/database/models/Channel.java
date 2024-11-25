package ipp.estg.database.models;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final int id;
    private final int ownerId;
    private String name;
    private int port;
    private final List<Integer> participants; // Participants ids

    public Channel(int id, int ownerId, String name, int port, List<Integer> participants) {
        this.id = id;
        this.ownerId = ownerId;
        this.port = port;
        this.name = name;
        this.participants = participants;
    }

    public Channel(int id, int ownerId, String name, int port) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.port = port;
        this.participants = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getParticipants() {
        return participants;
    }

    public boolean isUserInChannel(int userId) {
        return participants.contains(userId);
    }
}
