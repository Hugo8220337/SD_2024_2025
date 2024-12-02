package ipp.estg.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {

    private final Logger logger;

    private AppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static AppLogger getLogger(Class<?> clazz) {
        return new AppLogger(clazz);
    }

    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    public void error(String message, Throwable throwable, Object... args) {
        logger.error(message, args, throwable);
    }
}
