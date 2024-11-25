package ipp.estg.commands.activatingEmergencyCommunication;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.ActivatingEmergencyCommunications;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

public class GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository;
    private final String[] inputArray;

    public GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand(WorkerThread workerThread, IUserRepository userRepository, IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.activatingEmergencyCommunicationsRepository = activatingEmergencyCommunicationsRepository;
        this.inputArray = inputArray;
    }

    private List<ActivatingEmergencyCommunications> getPendingEmergencyCommunications() {
        List<ActivatingEmergencyCommunications> pendingEmergencyCommunications = new ArrayList<>();
        pendingEmergencyCommunications = activatingEmergencyCommunicationsRepository.getPendingApprovals();
        return pendingEmergencyCommunications;
    }

    @Override
    public void execute() {
        int userId = Integer.parseInt(inputArray[1]);
        User requestingUser = userRepository.getById(userId);

        // Check if user has permission to approve
        if (requestingUser == null || !requestingUser.canApproveEmergencyCommunicationsRequests()) {
            workerThread.sendMessage("ERROR: User does not have permission to approve Activating Emergency Communications Requests");
            return;
        }

        // Mount Response with pending Activating Emergency Communications Requests
        JsonConverter converter = new JsonConverter();
        List<ActivatingEmergencyCommunications> pendingRequests = getPendingEmergencyCommunications();

        // Convert to JSON
        String json = converter.toJson(pendingRequests);

        // Send pending requests to the client
        workerThread.sendMessage(json);
    }
}
