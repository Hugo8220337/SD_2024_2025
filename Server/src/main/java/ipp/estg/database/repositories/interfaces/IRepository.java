package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

/**
 * Interface for a generic repository that provides basic CRUD operations.
 *
 * @param <T> The type of entity that the repository handles.
 */
public interface IRepository<T> {

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id The unique identifier of the entity.
     * @return The entity corresponding to the provided id.
     */
    T getById(int id);

    /**
     * Retrieves all entities of the specified type.
     *
     * @return A list of all entities.
     */
    List<T> getAll();

    /**
     * Removes an entity by its unique identifier.
     *
     * @param id The unique identifier of the entity to remove.
     * @throws CannotWritetoFileException if there is an error while writing to the file or database.
     */
    void remove(int id) throws CannotWritetoFileException;
}
