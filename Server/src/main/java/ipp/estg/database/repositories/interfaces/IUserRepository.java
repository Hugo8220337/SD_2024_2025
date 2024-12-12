package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

/**
 * Interface for the UserRepository which extends the generic IRepository interface.
 * Provides methods for user-specific operations such as login, user management, and approval.
 */
public interface IUserRepository extends IRepository<User> {

    /**
     * Allows a user to log in by providing their email and password.
     *
     * @param email    The email of the user.
     * @param password The password of the user.
     * @return The user corresponding to the provided email and password.
     */
    User login(String email, String password);

    /**
     * Adds a new user to the repository.
     *
     * @param username  The username of the user.
     * @param email     The email of the user.
     * @param password  The password of the user.
     * @param userType  The type of user (e.g., admin, regular).
     * @return True if the user is added successfully, otherwise false.
     * @throws CannotWritetoFileException if there is an error while writing to the file or database.
     */
    boolean add(
            String username,
            String email,
            String password,
            UserTypes userType
    ) throws CannotWritetoFileException;

    /**
     * Updates the status of a user (e.g., approval of a user).
     *
     * @param userToApprove The user to be updated.
     * @throws CannotWritetoFileException if there is an error while writing to the file or database.
     */
    void update(User userToApprove) throws CannotWritetoFileException;

    /**
     * Retrieves a user by their email address.
     *
     * @param userEmail The email address of the user.
     * @return The user corresponding to the provided email.
     */
    User getByEmail(String userEmail);

    /**
     * Retrieves a list of users that are pending approval, based on user type.
     *
     * @param userType The type of user to filter (e.g., admin, regular).
     * @return A list of users pending approval.
     */
    List<User> getPendingUsers(UserTypes userType);

}
