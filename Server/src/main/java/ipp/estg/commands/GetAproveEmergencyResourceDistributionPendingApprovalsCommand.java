package ipp.estg.commands;

import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

public class GetAproveEmergencyResourceDistributionPendingApprovalsCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IEmergencyResourceDistributionRepository emergencyRepository;
    private final String[] inputArray;

    public GetAproveEmergencyResourceDistributionPendingApprovalsCommand(WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.inputArray = inputArray;
    }

    private List<EmergencyResourceDistribution> getPendingApprovals() {
        List<EmergencyResourceDistribution> pendingEmergency = new ArrayList<>();
        pendingEmergency = emergencyRepository.getPendingApprovals();
        return pendingEmergency;
    }

    @Override
    public void execute() {

        int userId = Integer.parseInt(inputArray[1]);
        User requestingUser = userRepository.getById(userId);

        // Check if user has permission to approve
        if (requestingUser == null || !requestingUser.canApproveEmergencyResourceDistributionRequests()) {
            workerThread.sendMessage("ERROR: User does not have permission to approve Emergency Resource Distribution Requests");
            return;
        }

        // Mount Response with pending Emergency Resource Distribution Requests
        JsonConverter converter = new JsonConverter();
        List<EmergencyResourceDistribution> pendingRequests = getPendingApprovals();

        // Convert to JSON
        String json = converter.toJson(pendingRequests);

        // Send pending requests to the client
        workerThread.sendMessage(json);
    }
}
