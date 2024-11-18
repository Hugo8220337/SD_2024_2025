package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IUserRepository extends IRepository<User> {
    User login(String email, String password);

    boolean add(
            String username,
            String email,
            String password,
            UserTypes userType
    ) throws CannotWritetoFileException;

    void update(User userToApprove) throws CannotWritetoFileException;

    User getByEmail(String userEmail);

    List<User> getPendingUsers(UserTypes userType);

}
