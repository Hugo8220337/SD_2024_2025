package ipp.estg.database.models;

import ipp.estg.database.models.enums.UserTypes;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String username;
    private String email;
    private String password;
    private UserTypes userType;
    private boolean isApproved;
    private int approvedBy; // id of the user that approved this user

    public User(int id, String username, String email, String password, UserTypes userType, Boolean isApproved) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;

        this.isApproved = isApproved;
        this.approvedBy = 99999;
    }

    public User(int id, String username, String email, String password, UserTypes userType) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;

        this.isApproved = userType == UserTypes.Low; // LOW type is auto-approved
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


    public String getPassword() {
        return password;
    }

    public UserTypes getUserType() {
        return userType;
    }


    public boolean isApproved() {
        return isApproved;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApproved(boolean approved, int aprovedBy) {
        this.isApproved = approved;
        this.approvedBy = aprovedBy;
    }


    public boolean canApproveUsers(UserTypes typeToApprove) {
        return switch (userType) {
            case High -> true;
            case Medium -> typeToApprove == UserTypes.Medium;
            case Low -> false;
        };
    }

    public boolean canApproveMassEvacuationRequests() {
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
