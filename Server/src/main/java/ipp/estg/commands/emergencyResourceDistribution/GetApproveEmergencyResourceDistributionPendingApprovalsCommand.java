package ipp.estg.commands.emergencyResourceDistribution;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.List;

/**
 * Command class that handles the request for approving emergency resource distribution requests.
 * This class executes the logic for getting all pending emergency resource distribution requests
 * that require approval.
 */
public class GetApproveEmergencyResourceDistributionPendingApprovalsCommand implements ICommand {

    /**
     * Worker thread that is executing the command
     */
    private final WorkerThread workerThread;

    /**
     * User repository to access the database
     */
    private final IUserRepository userRepository;

    /**
     * Emergency Resource Distribution repository to access the database
     */
    private final IEmergencyResourceDistributionRepository emergencyRepository;

    /**
     * Logger for the command
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(GetApproveEmergencyResourceDistributionPendingApprovalsCommand.class);

    /**
     * Constructor to initialize the command with necessary dependencies.
     *
     * @param workerThread the worker thread executing the command
     * @param userRepository the user repository to access the database
     * @param emergencyRepository the emergency resource distribution repository to access the database
     */
    public GetApproveEmergencyResourceDistributionPendingApprovalsCommand(WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
    }

    /**
     * Get all pending emergency resource distribution requests that require approval.
     *
     * @return list of pending emergency resource distribution requests
     */
    private List<EmergencyResourceDistribution> getPendingApprovals() {
        List<EmergencyResourceDistribution> pendingEmergency;
        pendingEmergency = emergencyRepository.getPendingApprovals();
        return pendingEmergency;
    }

    /**
     * Execute the command to get all pending emergency resource distribution requests that require approval.
     */
    @Override
    public void execute() {
        int userId = workerThread.getCurrentUserId();
        User requestingUser = userRepository.getById(userId);

        // Check if user has permission to approve
        if (requestingUser == null || !requestingUser.canApproveEmergencyResourceDistributionRequests()) {
            workerThread.sendMessage("ERROR: User does not have permission to approve Emergency Resource Distribution Requests");
            LOGGER.error("User with id " + userId + " does not have permission to approve Emergency Resource Distribution Requests");
            return;
        }

        try {
            // Mount Response with pending Emergency Resource Distribution Requests
            JsonConverter converter = new JsonConverter();
            List<EmergencyResourceDistribution> pendingRequests = getPendingApprovals();

            // Convert to JSON
            String json = converter.toJson(pendingRequests);

            // Send pending requests to the client
            workerThread.sendMessage(json);
            LOGGER.info("Sent pending Emergency Resource Distribution Requests to user with id " + userId);
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get pending Emergency Resource Distribution Requests");
            LOGGER.error("Could not get pending Emergency Resource Distribution Requests: " + e.getMessage());
        }
    }
}
