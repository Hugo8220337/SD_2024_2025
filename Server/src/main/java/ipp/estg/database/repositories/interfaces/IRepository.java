package ipp.estg.database.repositories.interfaces;

import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;

import java.util.List;

public interface IRepository<T> {
    T getById(int id);

    List<T> getAll();

    void remove(int id) throws CannotWritetoFileException;
}
