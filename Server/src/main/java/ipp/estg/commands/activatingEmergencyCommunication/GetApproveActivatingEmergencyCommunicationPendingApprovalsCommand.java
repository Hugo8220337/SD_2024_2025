package ipp.estg.commands.activatingEmergencyCommunication;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.ActivatingEmergencyCommunications;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

public class GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand.class);

    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository;

    public GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand(WorkerThread workerThread, IUserRepository userRepository, IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.activatingEmergencyCommunicationsRepository = activatingEmergencyCommunicationsRepository;
    }

    private List<ActivatingEmergencyCommunications> getPendingEmergencyCommunications() {
        List<ActivatingEmergencyCommunications> pendingEmergencyCommunications;
        pendingEmergencyCommunications = activatingEmergencyCommunicationsRepository.getPendingApprovals();
        return pendingEmergencyCommunications;
    }

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
        if (requestingUser == null || !requestingUser.canApproveEmergencyCommunicationsRequests()) {
            workerThread.sendMessage("ERROR: User does not have permission to approve Activating Emergency Communications Requests");
            LOGGER.error("User with id " + userId + " does not have permission to approve Activating Emergency Communications Requests");
            return;
        }

        try {
            // Mount Response with pending Activating Emergency Communications Requests
            JsonConverter converter = new JsonConverter();
            List<ActivatingEmergencyCommunications> pendingRequests = getPendingEmergencyCommunications();

            // Convert to JSON
            String json = converter.toJson(pendingRequests);

            // Send pending requests to the client
            workerThread.sendMessage(json);
            LOGGER.info("Sent pending Activating Emergency Communications Requests to user " + requestingUser.getUsername());
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get pending Activating Emergency Communications Requests");
            LOGGER.error("Could not get pending Activating Emergency Communications Requests");
        }
    }
}
