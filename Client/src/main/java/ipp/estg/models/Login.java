package ipp.estg.models;

public class Login {
    private String userId;
    private UserTypes userType;
    private int privateMessagePort;


    public Login(String userId, UserTypes userType, int privateMessagePort) {
        this.userId = userId;
        this.userType = userType;
        this.privateMessagePort = privateMessagePort;
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

    public int getPrivateMessagePort() {
        return privateMessagePort;
    }

    public void setPrivateMessagePort(int privateMessagePort) {
        this.privateMessagePort = privateMessagePort;
    }
}
