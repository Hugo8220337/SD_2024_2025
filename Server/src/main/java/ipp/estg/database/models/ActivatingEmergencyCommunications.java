package ipp.estg.database.models;

import java.io.Serializable;

/**
 * This class represents the model for Activating Emergency Communications.
 * It contains the information related to the creation of emergency communication requests,
 * including the message and the users involved in the approval process.
 * The model supports two constructors:
 * <ul>
 *     <li>One for creating a communication with both creator and approver IDs.</li>
 *     <li>Another for creating a communication with just the creator ID, where the approver ID is set to a default value of -1.</li>
 * </ul>
 */
public class ActivatingEmergencyCommunications implements Serializable {

    /**
     * The unique identifier for the emergency communication request.
     */
    private final int id;

    /**
     * The message associated with the emergency communication request.
     */
    private final String message;

    /**
     * The ID of the user who created the emergency communication request.
     */
    private final int creatorId;

    /**
     * The ID of the user who approved the emergency communication request (optional).
     * Default value is -1 when not approved yet.
     */
    private int approverId;

    /**
     * Constructor to initialize the ActivatingEmergencyCommunications object with the creator and approver IDs.
     *
     * @param id the unique identifier of the emergency communication request.
     * @param message the message associated with the emergency communication request.
     * @param creatorId the ID of the user who created the communication request.
     * @param approverId the ID of the user who approved the communication request.
     */
    public ActivatingEmergencyCommunications(int id, String message, int creatorId, int approverId) {
        this.id = id;
        this.message = message;
        this.creatorId = creatorId;
        this.approverId = approverId;
    }

    /**
     * Constructor to initialize the ActivatingEmergencyCommunications object with only the creator ID.
     * The approver ID is set to -1 (default value).
     *
     * @param id the unique identifier of the emergency communication request.
     * @param message the message associated with the emergency communication request.
     * @param creatorId the ID of the user who created the communication request.
     */
    public ActivatingEmergencyCommunications(int id, String message, int creatorId) {
        this.id = id;
        this.creatorId = creatorId;
        this.message = message;
        this.approverId = -1;
    }

    /**
     * Gets the unique identifier for the emergency communication request.
     *
     * @return the ID of the emergency communication request.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the message associated with the emergency communication request.
     *
     * @return the message of the emergency communication request.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the ID of the user who created the emergency communication request.
     *
     * @return the ID of the creator of the emergency communication request.
     */
    public int getCreatorId() {
        return creatorId;
    }

    /**
     * Gets the ID of the user who approved the emergency communication request.
     *
     * @return the ID of the approver of the emergency communication request.
     */
    public int getApproverId() {
        return approverId;
    }

    /**
     * Sets the ID of the user who approved the emergency communication request.
     *
     * @param approverId the ID of the approver of the emergency communication request.
     */
    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    /**
     * Returns a string representation of the ActivatingEmergencyCommunications object.
     *
     * @return a string representation of the emergency communication.
     */
    @Override
    public String toString() {
        return "ActivatingEmergencyCommunications{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", approverId='" + approverId + '\'' +
                '}';
    }
}
