/**
 * Command to handle user registration.
 * This class implements the ICommand interface and provides functionality to register a new user
 * into the system by adding them to the database.
 */
package ipp.estg.commands.auth;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

/**
 * Command to handle user registration.
 */
public class RegisterCommand implements ICommand {

    /**
     * The worker thread handling the current request.
     */
    private final WorkerThread workerThread;

    /**
     * Repository interface to manage user-related database operations.
     */
    private final IUserRepository IUserRepository;

    /**
     * Array containing the input arguments provided by the client.
     * Expected input format: ["register", username, email, password, userType].
     */
    private final String[] inputArray;

    /**
     * Logger instance for logging relevant information and errors.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(RegisterCommand.class);

    /**
     * Constructs a new RegisterCommand instance.
     *
     * @param workerThread    the current worker thread handling the request.
     * @param IUserRepository the user repository interface to perform database operations.
     * @param inputArray      the input arguments provided by the client.
     */
    public RegisterCommand(WorkerThread workerThread, IUserRepository IUserRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.IUserRepository = IUserRepository;
        this.inputArray = inputArray;
    }

    /**
     * Executes the registration command.
     * This method attempts to register a new user with the provided details.
     * If the user already exists, an error message is sent back to the client.
     */
    @Override
    public void execute() {
        String username = inputArray[1];
        String email = inputArray[2];
        String password = inputArray[3];
        UserTypes userType = UserTypes.getUserType(inputArray[4]);

        try {
            // insert user in the database
            boolean addedUser = IUserRepository.add(username, email, password, userType);
            if (addedUser) {
                workerThread.sendMessage("SUCCESS");
                LOGGER.info("User " + username + " registered successfully");
            } else {
                workerThread.sendMessage("ERROR: User already exists");
                LOGGER.error("User " + username + " already exists");
            }
        } catch (CannotWritetoFileException cwtfe) {
            LOGGER.error("Error while writing to file: " + cwtfe.getMessage());
            workerThread.sendMessage("ERROR: Could not register user");
        }
    }
}
