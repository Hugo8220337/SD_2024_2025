package ipp.estg.commands.massEvacuation;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to get all pending Mass Evacuation Requests
 */
public class GetMassEvacuationPendingApprovalsCommand implements ICommand {

    /**
     * Worker Thread that requested the command
     */
    private final WorkerThread workerThread;

    /**
     * Repository to access User data
     */
    private final IUserRepository userRepository;

    /**
     * Repository to access Mass Evacuation data
     */
    private final IMassEvacuationRepository evacuationsRepository;

    /**
     * Logger for the command class
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(GetMassEvacuationPendingApprovalsCommand.class);

    /**
     * Constructor
     * @param workerThread Worker Thread that requested the command
     * @param userRepository Repository to access User data
     * @param evacuationsRepository Repository to access Mass Evacuation data
     */
    public GetMassEvacuationPendingApprovalsCommand(WorkerThread workerThread, IUserRepository userRepository, IMassEvacuationRepository evacuationsRepository) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.evacuationsRepository = evacuationsRepository;
    }

    /**
     * Get all pending Mass Evacuation Requests
     * @return List of pending Mass Evacuation Requests
     */
    private List<MassEvacuation> getPendingEvacuations() {
        List<MassEvacuation> pendingEvacuations = new ArrayList<>();
        pendingEvacuations = evacuationsRepository.getPendingApprovals();
        return pendingEvacuations;
    }

    /**
     * Execute the command to get all pending Mass Evacuation Requests
     */
    @Override
    public void execute() {
        int userId = workerThread.getCurrentUserId();
        if (userId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        User requestingUser = userRepository.getById(userId);

        // Check if user has permission to approve
        if (requestingUser == null || !requestingUser.canApproveMassEvacuationRequests()) {
            workerThread.sendMessage("ERROR: User does not have permission to approve Mass Evacuation Requests");
            LOGGER.error("User with id " + userId + " does not have permission to approve Mass Evacuation Requests");
            return;
        }

        try {
            // Mount Response with pending Mass Evacuations Requests
            JsonConverter converter = new JsonConverter();
            List<MassEvacuation> pendingRequests = getPendingEvacuations();

            // Convert to JSON
            String json = converter.toJson(pendingRequests);

            // Send pending requests to the client
            workerThread.sendMessage(json);
            LOGGER.info("Sent pending Mass Evacuation Requests to user with id " + userId);
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get pending Mass Evacuation Requests");
            LOGGER.error("Could not get pending Mass Evacuation Requests: " + e.getMessage());
        }
    }
}
