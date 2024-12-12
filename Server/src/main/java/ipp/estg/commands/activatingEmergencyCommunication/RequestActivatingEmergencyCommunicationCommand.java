/**
 * Command to handle requests for activating emergency communications.
 * This command validates user permissions and processes the request,
 * allowing certain users to broadcast messages immediately or queue them for approval.
 */
package ipp.estg.commands.activatingEmergencyCommunication;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

/**
 * Command to handle user requests for activating emergency communications.
 */
public class RequestActivatingEmergencyCommunicationCommand implements ICommand {

    /** Logger for the command. */
    private static final AppLogger LOGGER = AppLogger.getLogger(RequestActivatingEmergencyCommunicationCommand.class);

    /** The worker thread handling the user's request. */
    private final WorkerThread workerThread;

    /** Repository for emergency communications-related operations. */
    private final IActivatingEmergencyCommunicationsRepository emergencyCommunicationsRepository;

    /** Repository for user-related operations. */
    private final IUserRepository userRepository;

    /** Input data for the command. */
    private final String[] inputArray;

    /** Reference to the server for broadcasting messages. */
    private final Server server;

    /**
     * Constructor to initialize dependencies for the command.
     *
     * @param workerThread                 the worker thread handling the request
     * @param userRepository               the repository for user-related operations
     * @param emergencyCommunicationsRepository the repository for emergency communications-related operations
     * @param inputArray                   the input data for the command
     * @param server                       the server for broadcasting messages
     */
    public RequestActivatingEmergencyCommunicationCommand(WorkerThread workerThread, IUserRepository userRepository, IActivatingEmergencyCommunicationsRepository emergencyCommunicationsRepository, String[] inputArray, Server server) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyCommunicationsRepository = emergencyCommunicationsRepository;
        this.inputArray = inputArray;
        this.server = server;
    }

    /**
     * Executes the command to handle the request for activating emergency communications.
     * Validates the user's permissions and processes the request based on user type.
     */
    @Override
    public void execute() {
        int requesterId = workerThread.getCurrentUserId();
        if (requesterId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        String message = inputArray[1];
        LOGGER.info("ActivatingEmergencyCommunicationCommand started for user with id: " + requesterId);

        User requester = userRepository.getById(requesterId);
        if (requester.getUserType().equals(UserTypes.All)) {
            workerThread.sendMessage("ERROR: User does not have permission to request");
            LOGGER.error("User with id " + requesterId + " does not have permission to request");
            return;
        }

        try {
            // Add Activating Emergency Communications request
            // high and medium users can activate without approval
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.High) || requester.getUserType().equals(UserTypes.Medium)) {
                // If requester is high, add approver id
                wasAddSuccessful = emergencyCommunicationsRepository.add(message, requesterId);

                server.sendBrodcastMessage(message);
                LOGGER.info("Broadcasted message: " + message);
            }
            else {
                // If requester is medium, add without approver id, so it can be approved later
                wasAddSuccessful = emergencyCommunicationsRepository.add(message, requesterId);
                LOGGER.info("Added message to be approved later: " + message);
            }

            String response = wasAddSuccessful
                    ? "SUCCESS: Activating Emergency Communications requested"
                    : "ERROR: Activating Emergency Communications request failed";
            workerThread.sendMessage(response);
            LOGGER.info(response + " user with id: " + requesterId);
        } catch (CannotWritetoFileException e) {
            LOGGER.error("Error approving user", e);
            workerThread.sendMessage("ERROR: Activating Emergency Communications request failed");
        }

    }
}
