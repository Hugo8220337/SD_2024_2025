package ipp.estg.models;

public class Login {
    private String userId;
    private UserTypes userType;

    public Login(String userId, UserTypes userType) {
        this.userId = userId;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public UserTypes getUserType() {
        return userType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }
}
