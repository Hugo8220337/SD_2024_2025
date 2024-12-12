/**
 * Command to handle user login requests.
 * Validates user credentials, checks if the user is approved, and establishes
 * the user's session by setting their ID in the worker thread.
 */
package ipp.estg.commands.auth;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.dto.LoginResponseDto;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

/**
 * Command to handle user login requests.
 */
public class LoginICommand implements ICommand {

    /** The worker thread handling the user's request. */
    private final WorkerThread workerThread;

    /** Repository for user-related operations. */
    private final IUserRepository userRepository;

    /** Array containing input data for the command. */
    private final String[] inputArray;

    /** Logger for the command. */
    private static final AppLogger LOGGER = AppLogger.getLogger(LoginICommand.class);

    /**
     * Constructor to initialize dependencies for the command.
     *
     * @param workerThread the worker thread handling the request
     * @param userRepository the repository for user-related operations
     * @param inputArray the input data for the command
     */
    public LoginICommand(WorkerThread workerThread, IUserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
    }

    /**
     * Executes the command to handle user login requests.
     * Validates the user's credentials, checks if the user is approved, and sets their session ID.
     */
    @Override
    public void execute() {
        String email = inputArray[1];
        String password = inputArray[2];

        User user = userRepository.login(email, password);


        if (user == null) {
            workerThread.sendMessage("ERROR: Invalid email or password");
            LOGGER.error("Invalid email or password");
            return;
        }

        if (!user.isApproved()) {
            workerThread.sendMessage("ERROR: User not approved");
            LOGGER.error("User not approved");
            return;
        }

        // Set current user id in worker thread, for future use
        workerThread.setCurrentUserId(user.getId());

        try {
            // Mount response
            JsonConverter converter = new JsonConverter();
            String jsonResponse = converter.toJson(new LoginResponseDto(
                    Integer.toString(user.getId()),
                    user.getUserType().toString()
            ));

            // Send Response
            workerThread.sendMessage(jsonResponse);

            LOGGER.info("User with id " + user.getId() + " logged in");
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get channels");
            LOGGER.error("Could not get channels: " + e.getMessage());
        }
    }
}
