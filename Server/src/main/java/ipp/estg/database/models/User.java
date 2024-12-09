package ipp.estg.database.models;

import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.utils.EncryptPassword;

import java.io.Serializable;

public class User implements Serializable {

    private final int id;
    private final String username;
    private final String email;
    private final String password;
    private final UserTypes userType;
    private final int privateMessagePort; // port to send private messages
    private boolean isApproved;
    private int approvedBy; // id of the user that approved this user

    public User(int id, String username, String email, String password, UserTypes userType, int privateMessagePort, Boolean isApproved) {
        this.id = id;
        this.email = email;
        this.username = username;
        //this.password = password;
        this.password = EncryptPassword.hashPassword(password); // Criptografa a password
        this.userType = userType;
        this.privateMessagePort = privateMessagePort;

        this.isApproved = isApproved;
        this.approvedBy = 99999;
    }

    public User(int id, String username, String email, String password, UserTypes userType, int privateMessagePort) {
        this.id = id;
        this.email = email;
        this.username = username;
        //this.password = password;
        this.password = EncryptPassword.hashPassword(password); // Criptografa a password
        this.userType = userType;
        this.privateMessagePort = privateMessagePort;

        this.isApproved = userType == UserTypes.All; // LOW type is auto-approved
        this.approvedBy = -1; // -1 means auto-approved
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {return password;}

    public UserTypes getUserType() {
        return userType;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public int getApprovedBy() {
        return approvedBy;
    }


    public int getPrivateMessagePort() { return privateMessagePort; }

    public void setApproved(boolean approved, int aprovedBy) {
        this.isApproved = approved;
        this.approvedBy = aprovedBy;
    }


    public boolean canApproveUsers(UserTypes typeToApprove) {
        return switch (userType) {
            case High -> true;
            case Medium -> typeToApprove == UserTypes.Medium;
            case Low -> false; // TODO
            case All -> false;
        };
    }

    public boolean canApproveMassEvacuationRequests() {
        return userType == UserTypes.High;
    }

    public boolean canApproveEmergencyResourceDistributionRequests() {
        return userType != UserTypes.All;
    }

    public boolean canApproveEmergencyCommunicationsRequests() {
        return userType == UserTypes.Medium || userType == UserTypes.High;
    }

    public boolean canCreateChannels() {
        return userType == UserTypes.High || userType == UserTypes.Medium;
    }

    public boolean canDeleteSomeoneElseChannel() {
        return userType == UserTypes.High;
    }

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
