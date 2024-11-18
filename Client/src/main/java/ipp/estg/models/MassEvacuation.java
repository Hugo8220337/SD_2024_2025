package ipp.estg.models;

public class MassEvacuation {
    private final int id;
    private final String message;
    private int approverId;

    public MassEvacuation(int id, String message, int approverId) {
        this.id = id;
        this.message = message;
        this.approverId = approverId;
    }

    public MassEvacuation(int id, String message) {
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
        return "MassEvacuation{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", approverId='" + approverId + '\'' +
                '}';
    }
}
