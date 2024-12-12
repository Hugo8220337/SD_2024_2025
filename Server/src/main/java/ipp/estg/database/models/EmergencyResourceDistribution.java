package ipp.estg.database.models;

import java.io.Serializable;

/**
 * This class represents the model for Emergency Resource Distribution.
 * It contains the message related to the distribution, along with the creator and approver IDs.
 */
public class EmergencyResourceDistribution implements Serializable {

    /**
     * The unique identifier for the emergency resource distribution.
     */
    private final int id;

    /**
     * The message associated with the emergency resource distribution.
     */
    private final String message;

    /**
     * The ID of the user who created the emergency resource distribution.
     */
    private final int creatorId;

    /**
     * The ID of the user who approved the emergency resource distribution (optional).
     * Default value is -1 when not approved yet.
     */
    private int approverId;

    /**
     * Constructor to initialize an EmergencyResourceDistribution object with the given parameters.
     *
     * @param id the unique identifier for the emergency resource distribution.
     * @param message the message related to the emergency resource distribution.
     * @param creatorId the ID of the user who created the distribution.
     * @param approverId the ID of the user who approved the distribution.
     */
    public EmergencyResourceDistribution(int id, String message, int creatorId, int approverId) {
        this.id = id;
        this.message = message;
        this.creatorId = creatorId;
        this.approverId = approverId;
    }

    /**
     * Constructor to initialize an EmergencyResourceDistribution object with the given parameters.
     * The approver ID is set to -1, indicating the distribution has not been approved yet.
     *
     * @param id the unique identifier for the emergency resource distribution.
     * @param message the message related to the emergency resource distribution.
     * @param creatorId the ID of the user who created the distribution.
     */
    public EmergencyResourceDistribution(int id, String message, int creatorId) {
        this.id = id;
        this.message = message;
        this.creatorId = creatorId;
        this.approverId = -1;
    }

    /**
     * Gets the unique identifier for the emergency resource distribution.
     *
     * @return the ID of the emergency resource distribution.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the message related to the emergency resource distribution.
     *
     * @return the message for the emergency resource distribution.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the ID of the user who created the emergency resource distribution.
     *
     * @return the ID of the creator of the distribution.
     */
    public int getCreatorId() {
        return creatorId;
    }

    /**
     * Gets the ID of the user who approved the emergency resource distribution.
     *
     * @return the ID of the approver, or -1 if not yet approved.
     */
    public int getApproverId() {
        return approverId;
    }

    /**
     * Sets the ID of the user who approved the emergency resource distribution.
     *
     * @param approverId the ID of the approver.
     */
    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    /**
     * Returns a string representation of the EmergencyResourceDistribution object.
     *
     * @return a string containing the ID, message, and approver ID of the distribution.
     */
    @Override
    public String toString() {
        return "EmergencyResourceDistribution{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", approverId='" + approverId + '\'' +
                '}';
    }
}
