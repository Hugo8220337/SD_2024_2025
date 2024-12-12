package ipp.estg.utils;

import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading and writing object lists to and from files.
 * This class provides methods to read a list of objects from a file and write a list of objects to a file.
 *
 * @param <T> The type of objects to be handled in the file operations.
 */
public class FileUtils<T> {

    /**
     * The file path where the object list is stored.
     */
    private final String filePath;

    /**
     * Constructs a FileUtils object for a specified file path.
     *
     * @param filePath The path to the file where the object list will be read from or written to.
     */
    public FileUtils(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads a list of objects from a file.
     * The objects are deserialized from the file and returned as a list.
     * If an error occurs during reading, an empty list is returned.
     *
     * @return A list of objects read from the file. Returns an empty list if the file cannot be read.
     */
    public List<T> readObjectListFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>(); // Retorna lista vazia se não conseguir ler
        }
    }

    /**
     * Writes a list of objects to a file.
     * The objects are serialized and written to the specified file path.
     * If the file's parent directory doesn't exist, it is created.
     *
     * @param records The list of objects to write to the file.
     * @return true if the objects were successfully written to the file.
     * @throws CannotWritetoFileException If there is an error during the writing process.
     */
    public boolean writeObjectListToFile(List<T> records) throws CannotWritetoFileException {
        File file = new File(filePath);
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs(); // se não encontrar o ficheiro, vai riá-lo
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(records);
            return true;
        } catch (IOException e) {
            throw new CannotWritetoFileException("Cannot write to file", e.getMessage());
        }
    }
}
