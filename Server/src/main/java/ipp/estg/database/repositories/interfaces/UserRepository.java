package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface UserRepository {
    User login(String email, String password);

    boolean addUser(
            String username,
            String email,
            String password,
            UserTypes userType
    ) throws CannotWritetoFileException;

    boolean removeUser(int id) throws CannotWritetoFileException;

    List<User> getAllUsers();

    User getUserByEmail(String userEmail);

    List<User> getPendingUsers(UserTypes userType);

    void updateUser(User userToApprove) throws CannotWritetoFileException;
}
