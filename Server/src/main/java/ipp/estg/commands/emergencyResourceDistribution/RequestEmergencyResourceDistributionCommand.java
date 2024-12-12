package ipp.estg.commands.emergencyResourceDistribution;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

/**
 * Command class that handles the request for emergency resource distribution.
 * This class executes the logic for adding a new emergency resource distribution request,
 * either requiring approval or not, based on the user type.
 */
public class RequestEmergencyResourceDistributionCommand implements ICommand {

    /**
     * Worker thread that is executing the command
     */
    private final WorkerThread workerThread;

    /**
     * User repository to access the database
     */
    private final IEmergencyResourceDistributionRepository emergencyRepository;

    /**
     * User repository to access the database
     */
    private final IUserRepository userRepository;

    /**
     * Input array with the command arguments
     */
    private final String[] inputArray;

    /**
     * Server object to send broadcast messages.
     */
    private final Server server;

    /**
     * Logger for the command
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(RequestEmergencyResourceDistributionCommand.class);

    /**
     * Constructor to initialize the command with necessary dependencies.
     *
     * @param workerThread the worker thread executing the command
     * @param userRepository the user repository to access the database
     * @param emergencyRepository the emergency resource distribution repository to access the database
     * @param inputArray the input array with the command arguments
     * @param server the server to send broadcast messages
     */
    public RequestEmergencyResourceDistributionCommand(WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository, String[] inputArray, Server server) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.inputArray = inputArray;
        this.server = server;
    }

    /**
     * Execute the command logic.
     * This method adds a new emergency resource distribution request to the database.
     * The request can be added with or without requiring approval, based on the user type.
     */
    @Override
    public void execute() {
        int requesterId = workerThread.getCurrentUserId();
        String message = inputArray[1];
        User requester = userRepository.getById(requesterId);

        try {
            // Add Emergency Resource Distribution request
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.All) ) {
                // If requester is low, do not add approver id, it should wait for approval
                wasAddSuccessful = emergencyRepository.add(message, requesterId);
                LOGGER.info("Emergency Resource Distribution requested by low user by id" + requesterId);
            } else {
                // If requester is Low or Higher add approver id and send the boradcast, no need for approval
                wasAddSuccessful = emergencyRepository.add(message, requesterId, requesterId);
                server.sendBrodcastMessage(message);
                LOGGER.info("Emergency Resource Distribution requested by high user with id: " + requesterId);
            }

            String response = wasAddSuccessful
                    ? "SUCCESS: Emergency Resource Distribution requested"
                    : "ERROR: Emergency Resource Distribution request failed";
            workerThread.sendMessage(response);
            LOGGER.info(response + " by user with id: " + requesterId);
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Could not request Emergency Resource Distribution");
            LOGGER.error("Could not request Emergency Resource Distribution: " + e.getMessage());
        }
    }
}
