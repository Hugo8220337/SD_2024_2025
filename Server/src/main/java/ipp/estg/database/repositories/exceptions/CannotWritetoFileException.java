package ipp.estg.database.repositories.exceptions;

public class CannotWritetoFileException extends Exception {
    public CannotWritetoFileException(String message, String eMessage) {
        super(message);
    }
}
