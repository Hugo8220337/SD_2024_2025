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
    private String approvedBy; // email of the user that approved this user

//    public User(int id) {
//        this.id = id;
//    }

    public User(int id, String username, String email, String password, UserTypes userType) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;

        this.isApproved = userType == UserTypes.Low; // LOW type is auto-approved
        this.approvedBy = userType == UserTypes.Low ? "SYSTEM" : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserTypes getUserType() {
        return userType;
    }

    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved, String aprovedBy) {
        this.isApproved = approved;
        this.approvedBy = aprovedBy;
    }
}
