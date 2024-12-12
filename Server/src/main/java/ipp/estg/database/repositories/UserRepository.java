package ipp.estg.database.repositories;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.utils.EncryptPassword;
import ipp.estg.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing user data. This repository allows logging in, adding, removing,
 * retrieving users, and updating user information. It uses {@link FileUtils} to handle file
 * operations and {@link EncryptPassword} for password hashing.
 */
public class UserRepository implements IUserRepository {

    /**
     * File utility for handling user data.
     */
    private final FileUtils<User> fileUtils;

    /**
     * Constructor that initializes the repository with a specified file path.
     *
     * @param filePath The file path to read/write the user data.
     */
    public UserRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    /**
     * Logs in a user by verifying their email and password.
     * The password is hashed before comparison.
     *
     * @param email    The email of the user.
     * @param password The password of the user.
     * @return The user if login is successful, otherwise null.
     */
    @Override
    public synchronized User login(String email, String password) {
        List<User> users = fileUtils.readObjectListFromFile();
        String hashedPassword = EncryptPassword.hashPassword(password); // Criptografa a password
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(hashedPassword)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Adds a new user to the repository.
     *
     * @param username The username of the new user.
     * @param email    The email of the new user.
     * @param password The password of the new user.
     * @param userType The type of the user (e.g., admin, regular).
     * @return True if the user was successfully added, otherwise false.
     * @throws CannotWritetoFileException If there is an error writing to the file.
     */
    @Override
    public synchronized boolean add(
            String username,
            String email,
            String password,
            UserTypes userType
    ) throws CannotWritetoFileException {
        List<User> users = fileUtils.readObjectListFromFile();

        User newUser = new User(users.size() + 1, username, email, password, userType);
        //User newUser = new User(users.size() + 1, "admin", "admin@admin.com", "admin", UserTypes.High, true); // para criar admins em development sem aprovação
        users.add(newUser);

        return fileUtils.writeObjectListToFile(users);
    }

    /**
     * Removes a user from the repository by their ID.
     *
     * @param id The ID of the user to remove.
     * @throws CannotWritetoFileException If there is an error writing to the file.
     */
    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<User> users = fileUtils.readObjectListFromFile();
        users.removeIf(user -> user.getId() == id);

        fileUtils.writeObjectListToFile(users);
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return A list of all users.
     */
    @Override
    public synchronized List<User> getAll() {
        return fileUtils.readObjectListFromFile();
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param userEmail The email of the user to retrieve.
     * @return The user with the specified email, or null if not found.
     */
    @Override
    public synchronized User getByEmail(String userEmail) {
        List<User> users = getAll();
        for (User user : users) {
            if (user.getEmail().equals(userEmail)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user with the specified ID, or null if not found.
     */
    public synchronized User getById(int userId) {
        List<User> users = getAll();
        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retrieves all users of a specific type who are pending approval.
     *
     * @param userType The type of user to retrieve (e.g., admin).
     * @return A list of users of the specified type who are pending approval.
     */
    @Override
    public synchronized List<User> getPendingUsers(UserTypes userType) {
        List<User> users = getAll();
        List<User> pendingUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.isApproved() && user.getUserType() == userType) {
                pendingUsers.add(user);
            }
        }
        return pendingUsers;
    }

    /**
     * Updates the information of a user (e.g., approving a user).
     *
     * @param userToApprove The user with updated information.
     * @throws CannotWritetoFileException If there is an error writing to the file.
     */
    @Override
    public synchronized void update(User userToApprove) throws CannotWritetoFileException{
        List<User> users = getAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == userToApprove.getId()) {
                users.set(i, userToApprove);
                break;
            }
        }
        fileUtils.writeObjectListToFile(users); // update user  in file
    }
}
