package ipp.estg.dto;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a user response.
 * This class is used to encapsulate user information in a format suitable for sending as a response.
 */
public class UserResponseDto {

    /**
     * The ID of the user.
     */
    int id;

    /**
     * The username of the user.
     */
    String username;

    /**
     * The email of the user.
     */
    String email;

    /**
     * The type of the user (e.g., High, Medium, Low, All).
     */
    UserTypes userType;

    /**
     * Constructs a new UserResponseDto with the specified user details.
     *
     * @param id       The ID of the user.
     * @param username The username of the user.
     * @param email    The email of the user.
     * @param userType The type of the user.
     */
    public UserResponseDto(int id, String username, String email, UserTypes userType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    /**
     * Converts a User object to a UserResponseDto.
     *
     * @param user The User object to convert.
     * @return The corresponding UserResponseDto.
     */
    public static UserResponseDto fromUserToUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getUserType());
    }

    /**
     * Converts a list of User objects to a list of UserResponseDto objects.
     *
     * @param users The list of User objects to convert.
     * @return A list of corresponding UserResponseDto objects.
     */
    public static List<UserResponseDto> fromUserToUserResponseDto(List<User> users) {
        List<UserResponseDto> usersDtos = new ArrayList<>();
        for (User user : users) {
            usersDtos.add(
                    new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getUserType())
            );
        }
        return usersDtos;
    }
}
