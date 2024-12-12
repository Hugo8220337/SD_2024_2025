package ipp.estg.database.models;

import java.io.Serializable;

/**
 * This class represents the model for a Mass Evacuation.
 * It contains the message related to the evacuation, along with the creator and approver IDs.
 */
public class MassEvacuation implements Serializable {

    /**
     * The unique identifier for the mass evacuation.
     */
    private final int id;

    /**
     * The message associated with the mass evacuation.
     */
    private final String message;

    /**
     * The ID of the user who created the mass evacuation.
     */
    private int creatorId;

    /**
     * The ID of the user who approved the mass evacuation (optional).
     * Default value is -1 when not approved yet.
     */
    private int approverId;

    /**
     * Constructor to initialize a MassEvacuation object with the given parameters.
     *
     * @param id the unique identifier for the mass evacuation.
     * @param message the message related to the mass evacuation.
     * @param creatorId the ID of the user who created the evacuation.
     * @param approverId the ID of the user who approved the evacuation.
     */
    public MassEvacuation(int id, String message, int creatorId, int approverId) {
        this.id = id;
        this.message = message;
        this.creatorId = creatorId;
        this.approverId = approverId;
    }

    /**
     * Constructor to initialize a MassEvacuation object with the given parameters.
     * The approver ID is set to -1, indicating the evacuation has not been approved yet.
     *
     * @param id the unique identifier for the mass evacuation.
     * @param message the message related to the mass evacuation.
     * @param creatorId the ID of the user who created the evacuation.
     */
    public MassEvacuation(int id, String message, int creatorId) {
        this.id = id;
        this.message = message;
        this.creatorId = creatorId;
        this.approverId = -1;
    }

    /**
     * Gets the unique identifier for the mass evacuation.
     *
     * @return the ID of the mass evacuation.
     */
    public int getId() {
        return id;
    }


    /**
     * Gets the message related to the mass evacuation.
     *
     * @return the message for the mass evacuation.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the ID of the user who created the mass evacuation.
     *
     * @return the ID of the creator of the evacuation.
     */
    public int getCreatorId() {
        return creatorId;
    }

    /**
     * Sets the ID of the user who created the mass evacuation.
     *
     * @param creatorId the ID of the creator.
     */
    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * Gets the ID of the user who approved the mass evacuation.
     *
     * @return the ID of the approver, or -1 if not yet approved.
     */
    public int getApproverId() {
        return approverId;
    }

    /**
     * Sets the ID of the user who approved the mass evacuation.
     *
     * @param approverId the ID of the approver.
     */
    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    /**
     * Returns a string representation of the MassEvacuation object.
     *
     * @return a string containing the ID, message, and approver ID of the evacuation.
     */
    @Override
    public String toString() {
        return "MassEvacuation{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", approverId='" + approverId + '\'' +
                '}';
    }
}
