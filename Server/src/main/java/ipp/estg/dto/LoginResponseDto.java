package ipp.estg.dto;


/**
 * Data Transfer Object (DTO) for the login response.
 * This class is used to encapsulate the information sent as a response after a user successfully logs in.
 */
public class LoginResponseDto {

    /**
     * The user ID of the logged-in user.
     */
    private String userId;

    /**
     * The type of the logged-in user (e.g., admin, regular).
     */
    private String userType;

    /**
     * Constructs a new LoginResponseDto with the specified user ID and user type.
     *
     * @param userId   The user ID of the logged-in user.
     * @param userType The type of the logged-in user.
     */
    public LoginResponseDto(String userId, String userType) {
        this.userId = userId;
        this.userType = userType;
    }

    /**
     * Gets the user ID of the logged-in user.
     *
     * @return The user ID of the logged-in user.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the type of the logged-in user.
     *
     * @return The type of the logged-in user.
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Sets the user ID of the logged-in user.
     *
     * @param userId The user ID of the logged-in user.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Sets the type of the logged-in user.
     *
     * @param userType The type of the logged-in user.
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

}
