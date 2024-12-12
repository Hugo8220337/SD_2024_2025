package ipp.estg.database.repositories.exceptions;

/**
 * Exception thrown when a file cannot be read.
 */
public class CannotReadFileException extends Exception {
    public CannotReadFileException(String message) {
        super(message);
    }
}
