package ipp.estg.commands.emergencyResourceDistribution;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

public class GetAproveEmergencyResourceDistributionPendingApprovalsCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IEmergencyResourceDistributionRepository emergencyRepository;
    private static final AppLogger LOGGER = AppLogger.getLogger(GetAproveEmergencyResourceDistributionPendingApprovalsCommand.class);

    public GetAproveEmergencyResourceDistributionPendingApprovalsCommand(WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
    }

    private List<EmergencyResourceDistribution> getPendingApprovals() {
        List<EmergencyResourceDistribution> pendingEmergency;
        pendingEmergency = emergencyRepository.getPendingApprovals();
        return pendingEmergency;
    }

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
