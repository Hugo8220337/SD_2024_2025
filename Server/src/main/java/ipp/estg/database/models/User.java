package ipp.estg.database.models;

import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.utils.EncryptPassword;

import java.io.Serializable;

/**
 * This class represents a user in the system.
 * It includes user details such as username, email, password, and user type,
 * as well as the approval status and the approver's ID.
 */
public class User implements Serializable {

    /**
     * The unique identifier for the user.
     */
    private final int id;

    /**
     * The username of the user.
     */
    private final String username;

    /**
     * The email of the user.
     */
    private final String email;

    /**
     * The password of the user.
     */
    private final String password;

    /**
     * The type of the user.
     */
    private final UserTypes userType;

    /**
     * The approval status of the user.
     */
    private boolean isApproved;

    /**
     * The ID of the user that approved this user.
     */
    private int approvedBy; // id of the user that approved this user

    /**
     * Constructor to initialize a User object with the given parameters.
     * This constructor hashes the password and sets the approval status.
     *
     * @param id the unique identifier for the user.
     * @param username the username of the user.
     * @param email the email address of the user.
     * @param password the password of the user (will be hashed).
     * @param userType the type of the user (e.g., Low, Medium, High).
     * @param isApproved the approval status of the user.
     */
    public User(int id, String username, String email, String password, UserTypes userType, Boolean isApproved) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = EncryptPassword.hashPassword(password); // Criptografa a password
        this.userType = userType;

        this.isApproved = isApproved;
        this.approvedBy = 99999;
    }

    /**
     * Constructor to initialize a User object with the given parameters.
     * This constructor hashes the password and sets the approval status based on the user type.
     *
     * @param id the unique identifier for the user.
     * @param username the username of the user.
     * @param email the email address of the user.
     * @param password the password of the user (will be hashed).
     * @param userType the type of the user (e.g., Low, Medium, High).
     */
    public User(int id, String username, String email, String password, UserTypes userType) {
        this.id = id;
        this.email = email;
        this.username = username;
        //this.password = password;
        this.password = EncryptPassword.hashPassword(password); // Criptografa a password
        this.userType = userType;

        this.isApproved = userType == UserTypes.All; // LOW type is auto-approved
        this.approvedBy = -1; // -1 means auto-approved
    }

    /**
     * Gets the unique identifier for the user.
     *
     * @return the ID of the user.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the hashed password of the user.
     *
     * @return the hashed password.
     */
    public String getPassword() {return password;}

    /**
     * Gets the type of user (e.g., Low, Medium, High, or All).
     *
     * @return the user type.
     */
    public UserTypes getUserType() {
        return userType;
    }

    /**
     * Gets the approval status of the user.
     *
     * @return true if the user is approved, false otherwise.
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * Gets the ID of the user who approved this user.
     *
     * @return the ID of the approver, or -1 if not applicable.
     */
    public int getApprovedBy() {
        return approvedBy;
    }

    /**
     * Sets the approval status and the ID of the user who approved this user.
     *
     * @param approved the approval status of the user.
     * @param aprovedBy the ID of the user who approved this user.
     */
    public void setApproved(boolean approved, int aprovedBy) {
        this.isApproved = approved;
        this.approvedBy = aprovedBy;
    }

    /**
     * Determines whether the user can approve other users based on their type.
     *
     * @param typeToApprove the type of user to be approved.
     * @return true if the user can approve users of the given type, false otherwise.
     */
    public boolean canApproveUsers(UserTypes typeToApprove) {
        return switch (userType) {
            case High -> true;
            case Medium -> typeToApprove == UserTypes.Medium || typeToApprove == UserTypes.Low;
            case Low, All -> false;
        };
    }

    /**
     * Determines whether the user can approve mass evacuation requests.
     *
     * @return true if the user can approve mass evacuation requests, false otherwise.
     */
    public boolean canApproveMassEvacuationRequests() {
        return userType == UserTypes.High;
    }

    /**
     * Determines whether the user can approve emergency resource distribution requests.
     *
     * @return true if the user can approve emergency resource distribution requests, false otherwise.
     */
    public boolean canApproveEmergencyResourceDistributionRequests() {
        return userType != UserTypes.All;
    }

    /**
     * Determines whether the user can approve emergency communications requests.
     *
     * @return true if the user can approve emergency communications requests, false otherwise.
     */
    public boolean canApproveEmergencyCommunicationsRequests() {
        return userType == UserTypes.Medium || userType == UserTypes.High;
    }

    /**
     * Determines whether the user can approve emergency resource requests.
     *
     * @return true if the user can approve emergency resource requests, false otherwise.
     */
    public boolean canCreateChannels() {
        return userType == UserTypes.High || userType == UserTypes.Medium;
    }

    /**
     * Determines whether the user can approve emergency resource requests.
     *
     * @return true if the user can approve emergency resource requests, false otherwise.
     */
    public boolean canDeleteSomeoneElseChannel() {
        return userType == UserTypes.High;
    }

    /**
     * Returns a string representation of the User object.
     * The string includes the user's ID, username, email, password, user type, approval status, and approver ID.
     *
     * @return a string representation of the user.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                ", isApproved=" + isApproved +
                ", approvedBy=" + approvedBy +
                '}';
    }
}
