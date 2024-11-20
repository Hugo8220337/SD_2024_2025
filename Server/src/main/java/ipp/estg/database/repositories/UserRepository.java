package ipp.estg.database.repositories;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {
    private final FileUtils<User> fileUtils;

    public UserRepository(String filePath) {
        this.fileUtils = new FileUtils<>(filePath);
    }

    @Override
    public synchronized User login(String email, String password) {
        List<User> users = fileUtils.readObjectListFromFile();
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean add(
            String username,
            String email,
            String password,
            UserTypes userType
    ) throws CannotWritetoFileException {
        List<User> users = fileUtils.readObjectListFromFile();

        User newUser = new User(users.size() + 1, username, email, password, userType);
        //User newUser = new User(users.size() + 1, "admin", "admin@admin.com", "admin", UserTypes.High, true);
        users.add(newUser);

        return fileUtils.writeObjectListToFile(users);
    }

    @Override
    public synchronized void remove(int id) throws CannotWritetoFileException {
        List<User> users = fileUtils.readObjectListFromFile();
        users.removeIf(user -> user.getId() == id);

        fileUtils.writeObjectListToFile(users);
    }

    @Override
    public synchronized List<User> getAll() {
        return fileUtils.readObjectListFromFile();
    }

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

    public synchronized User getById(int userId) {
        List<User> users = getAll();
        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }


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
