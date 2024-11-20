package ipp.estg.database.models;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final int id;
    private String name;
    private int port;
    private boolean isOpen;
    private final List<Integer> participants; // Participants ids

    public Channel(int id, String name, int port, List<Integer> participants) {
        this.id = id;
        this.port = port;
        this.name = name;
        this.isOpen = true; // chats começam sempre abertos
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

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void closeChat() {
        this.isOpen = false;
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
