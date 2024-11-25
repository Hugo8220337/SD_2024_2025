package ipp.estg.models;

public class ActivatingEmergencyCommunications {
    private final int id;
    private final String message;
    private int approverId;

    public ActivatingEmergencyCommunications(int id, String message, int approverId) {
        this.id = id;
        this.message = message;
        this.approverId = approverId;
    }

    public ActivatingEmergencyCommunications(int id, String message) {
        this.id = id;
        this.message = message;
        this.approverId = -1;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    @Override
    public String toString() {
        return "ActivatingEmergencyCommunications{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", approverId='" + approverId + '\'' +
                '}';
    }


}
