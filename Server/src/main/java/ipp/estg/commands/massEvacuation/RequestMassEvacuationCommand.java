package ipp.estg.commands.massEvacuation;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

/**
 * Command to request a mass evacuation. This command allows users with sufficient
 * privileges to request a mass evacuation to be broadcasted to other users.
 * The request is processed differently depending on the user's role (High, Medium, Low).
 */
public class RequestMassEvacuationCommand implements ICommand {

    /**
     * Worker thread that is executing the command
     */
    private final WorkerThread workerThread;

    /**
     * Repository to access mass evacuation data
     */
    private final IMassEvacuationRepository evacuationRepository;

    /**
     * Repository to access user data
     */
    private final IUserRepository userRepository;

    /**
     * Array with the command and arguments
     */
    private final String[] inputArray;

    /**
     * The server instance that is responsible for broadcasting messages to other users.
     */
    private final Server server;

    /**
     * Logger instance for logging the actions taken in this command.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(RequestMassEvacuationCommand.class);

    /**
     * Constructor for initializing the command with necessary dependencies.
     *
     * @param workerThread The worker thread executing the command.
     * @param userRepository The repository for accessing user data.
     * @param evacuationRepository The repository for managing mass evacuation data.
     * @param inputArray The input arguments passed to the command (message).
     * @param server The server instance used to broadcast messages.
     */
    public RequestMassEvacuationCommand(WorkerThread workerThread, IUserRepository userRepository, IMassEvacuationRepository evacuationRepository, String[] inputArray, Server server) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.evacuationRepository = evacuationRepository;
        this.inputArray = inputArray;
        this.server = server;
    }

    /**
     * Executes the mass evacuation request, validating user permissions and adding the request
     * to the repository. The action is dependent on the user’s role (High, Medium, Low).
     *
     * - If the user has sufficient privileges, the request is added, and a broadcast is sent.
     * - If the user does not have permission, an error message is sent.
     *
     * If the request is successfully added, a confirmation message is sent to the user.
     * In case of failure, an error message is provided.
     */
    @Override
    public void execute() {
        String message = inputArray[1];

        int requesterId = workerThread.getCurrentUserId();
        if (requesterId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }


        User requester = userRepository.getById(requesterId);
        if (requester.getUserType().equals(UserTypes.All) || requester.getUserType().equals(UserTypes.Low)) {
            workerThread.sendMessage("ERROR: User does not have permission to request");
            LOGGER.error("User with id " + requesterId +  " does not have permission to request");
            return;
        }

        try {
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.High)) {
                // If requester is high, add approver id, no need for approval
                wasAddSuccessful = evacuationRepository.add(message, requesterId);
                server.sendBrodcastMessage(message);
                LOGGER.info("Broadcasted mass evacuation request");
            }
            else {
                // If requester is medium, add without approver id, so it can be approved later
                wasAddSuccessful = evacuationRepository.add(message, requesterId);
                LOGGER.info("Added mass evacuation request");
            }

            String response = wasAddSuccessful
                    ? "SUCCESS: Mass evacuation requested"
                    : "ERROR: Mass evacuation request failed";
            workerThread.sendMessage(response);
            LOGGER.info(response + " by user with id: " + requesterId);
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Could not request mass evacuation");
            LOGGER.error("Error adding mass evacuation request", e);
        }
    }
}
