package ipp.estg.dto.response;

public class LoginResponseDto {
    private String userId;
    private String userType;
    private int privateMessagePort;

    public LoginResponseDto(String userId, String userType, int privateMessagePort) {
        this.userId = userId;
        this.userType = userType;
        this.privateMessagePort = privateMessagePort;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getPrivateMessagePort() {
        return privateMessagePort;
    }

    public void setPrivateMessagePort(int privateMessagePort) {
        this.privateMessagePort = privateMessagePort;
    }
}
