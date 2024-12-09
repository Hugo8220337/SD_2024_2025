package ipp.estg.database.models;

import java.io.Serializable;

public class EmergencyResourceDistribution implements Serializable {

    private final int id;
    private final String message;
    private final int creatorId;
    private int approverId;

    public EmergencyResourceDistribution(int id, String message, int creatorId, int approverId) {
        this.id = id;
        this.message = message;
        this.creatorId = creatorId;
        this.approverId = approverId;
    }

    public EmergencyResourceDistribution(int id, String message, int creatorId) {
        this.id = id;
        this.message = message;
        this.creatorId = creatorId;
        this.approverId = -1;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    @Override
    public String toString() {
        return "EmergencyResourceDistribution{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", approverId='" + approverId + '\'' +
                '}';
    }
}
