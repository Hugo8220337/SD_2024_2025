package ipp.estg.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for logging messages using SLF4J (Simple Logging Facade for Java).
 * This class wraps around SLF4J's Logger to provide convenient logging methods
 * for various log levels (info, debug, warn, error).
 * It allows logging messages with optional parameters for dynamic message formatting.
 */
public class AppLogger {

    /**
     * The SLF4J Logger instance used to log messages.
     */
    private final Logger logger;

    /**
     * Private constructor that initializes the Logger instance.
     * This constructor is used internally to create an AppLogger instance for a given class.
     *
     * @param clazz The class for which the logger is created.
     */
    private AppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Factory method to create an AppLogger instance for a given class.
     * This method provides an easy way to obtain an AppLogger for any class in the application.
     *
     * @param clazz The class for which the logger is created.
     * @return A new AppLogger instance for the specified class.
     */
    public static AppLogger getLogger(Class<?> clazz) {
        return new AppLogger(clazz);
    }

    /**
     * Logs an informational message with optional parameters.
     * The message will be logged at the INFO level.
     *
     * @param message The message to log.
     * @param args Optional parameters for the message, which will be inserted into the message string.
     */
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    /**
     * Logs a debug message with optional parameters.
     * The message will be logged at the DEBUG level.
     *
     * @param message The message to log.
     * @param args Optional parameters for the message, which will be inserted into the message string.
     */
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    /**
     * Logs a warning message with optional parameters.
     * The message will be logged at the WARN level.
     *
     * @param message The message to log.
     * @param args Optional parameters for the message, which will be inserted into the message string.
     */
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    /**
     * Logs an error message with optional parameters.
     * The message will be logged at the ERROR level.
     *
     * @param message The message to log.
     * @param args Optional parameters for the message, which will be inserted into the message string.
     */
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    /**
     * Logs an error message with optional parameters and a throwable.
     * The message will be logged at the ERROR level along with the stack trace of the throwable.
     *
     * @param message The message to log.
     * @param throwable The throwable whose stack trace will be logged.
     * @param args Optional parameters for the message, which will be inserted into the message string.
     */
    public void error(String message, Throwable throwable, Object... args) {
        logger.error(message, args, throwable);
    }
}
