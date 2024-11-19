package ipp.estg.database.models;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final int id;
    private String name;
    private final List<Integer> participants; // Participants ids

    public Channel(int id, String name, List<Integer> participants) {
        this.id = id;
        this.name = name;
        this.participants = participants;
    }

    public Channel(int id, String name) {
        this.id = id;
        this.name = name;
        this.participants = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getParticipants() {
        return participants;
    }

    public boolean isUserInChannel() {
        for(int participant : participants) {
            if(participant == 1) {
                return true;
            }
        }
        return false;
    }
}
