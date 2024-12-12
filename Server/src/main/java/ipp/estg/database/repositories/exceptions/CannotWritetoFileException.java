package ipp.estg.database.repositories.exceptions;

/**
 * Exception thrown when a file cannot be written to.
 */
public class CannotWritetoFileException extends Exception {
    public CannotWritetoFileException(String message, String eMessage) {
        super(message);
    }
}
