package ipp.estg.commands;

import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

public class GetMassEvacuationPendingApprovalsCommand implements ICommand {
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final IMassEvacuationRepository evacuationsRepository;
    private final String[] inputArray;

    public GetMassEvacuationPendingApprovalsCommand(WorkerThread workerThread, IUserRepository userRepository, IMassEvacuationRepository evacuationsRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.evacuationsRepository = evacuationsRepository;
        this.inputArray = inputArray;
    }

    private List<MassEvacuation> getPendingEvacuations() {
        List<MassEvacuation> pendingEvacuations = new ArrayList<>();
        pendingEvacuations = evacuationsRepository.getPendingApprovals();
        return pendingEvacuations;
    }

    @Override
    public void execute() {
        int userId = Integer.parseInt(inputArray[1]);
        User requestingUser = userRepository.getById(userId);

        // Check if user has permission to approve
        if (requestingUser == null || !requestingUser.canApproveMassEvacuationRequests()) {
            workerThread.sendMessage("ERROR: User does not have permission to approve Mass Evacuation Requests");
            return;
        }

        // Mount Response with pending Mass Evacuations Requests
        JsonConverter converter = new JsonConverter();
        List<MassEvacuation> pendingRequests = getPendingEvacuations();

        // Convert to JSON
        String json = converter.toJson(pendingRequests);

        // Send pending requests to the client
        workerThread.sendMessage(json);
    }
}
