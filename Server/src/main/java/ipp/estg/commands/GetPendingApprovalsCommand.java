package ipp.estg.commands;

import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.UserRepository;
import ipp.estg.threads.WorkerThread;

import java.util.List;

public class GetPendingApprovalsCommand implements Command{
    private final WorkerThread workerThread;
    private final UserRepository userRepository;
    private final String[] inputArray;

    public GetPendingApprovalsCommand(WorkerThread workerThread, UserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
    }

    @Override
    public void execute() {
        String userEmail = inputArray[1];

        // Check if user has permission to approve
        User requestingUser = userRepository.getUserByEmail(userEmail);
        if (requestingUser == null || !workerThread.canApproveUsers(requestingUser.getUserType())) {
            workerThread.sendMessage("UNAUTHORIZED");
            return;
        }

        List<User> pendingUsers = userRepository.getPendingUsers(requestingUser.getUserType());
        StringBuilder response = new StringBuilder("PENDING_USERS ");
        for (User pendingUser : pendingUsers) {
            response.append(pendingUser.getEmail()).append(",");
        }

        workerThread.sendMessage(response.toString());
    }
}
