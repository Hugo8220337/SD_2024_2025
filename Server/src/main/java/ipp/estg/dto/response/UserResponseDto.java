package ipp.estg.dto.response;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;

import java.util.ArrayList;
import java.util.List;

public class UserResponseDto {
    int id;
    String username;
    String email;
    UserTypes userType;

    public UserResponseDto(int id, String username, String email, UserTypes userType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    public static UserResponseDto fromUserToUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getUserType());
    }

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
