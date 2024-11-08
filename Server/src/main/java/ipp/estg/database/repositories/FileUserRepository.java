package ipp.estg.database.repositories;

import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUserRepository implements UserRepository {
    private final String filePath;

    public FileUserRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public synchronized User login(String email, String password) {
        List<User> users = readUsersFromFile();
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean addUser(String username, String email, String password) throws CannotWritetoFileException {
        List<User> users = readUsersFromFile();

        User newUser = new User(users.size() + 1, username, email, password);

        users.add(newUser);

        return writeUsersToFile(users);
    }

    @Override
    public synchronized boolean removeUser(int id) throws CannotWritetoFileException {
        List<User> users = readUsersFromFile();
        users.removeIf(user -> user.getId() == id);

        return writeUsersToFile(users);
    }

    @Override
    public synchronized List<User> getAllUsers() {
        return readUsersFromFile();
    }

    private List<User> readUsersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>(); // Retorna lista vazia se não conseguir ler
        }
    }

    private boolean writeUsersToFile(List<User> users) throws CannotWritetoFileException {
        File file = new File(filePath);
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs(); // se não encontrar o ficheiro, vai riá-lo
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(users);
            return true;
        } catch (IOException e) {
            throw new CannotWritetoFileException("Cannot write to file", e.getMessage());
        }
    }
}
