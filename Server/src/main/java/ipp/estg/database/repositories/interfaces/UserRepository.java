package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface UserRepository {
    User login(String email, String password);

    boolean addUser(String username, String email, String password) throws CannotWritetoFileException;

    boolean removeUser(int id) throws CannotWritetoFileException;

    List<User> getAllUsers();
}
