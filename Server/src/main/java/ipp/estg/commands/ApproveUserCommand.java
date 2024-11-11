package ipp.estg.commands;

import ipp.estg.Server;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.UserRepository;
import ipp.estg.threads.WorkerThread;

public class ApproveUserCommand implements Command {

    private final WorkerThread workerThread;
    private final Server server;
    private final UserRepository userRepository;
    private final String[] inputArray;

    public ApproveUserCommand(WorkerThread workerThread, Server server, UserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.server = server;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
    }

    @Override
    public void execute() {
        String approverEmail = inputArray[1];
        String userToApproveEmail = inputArray[2];

        User approver = userRepository.getUserByEmail(approverEmail);
        User userToApprove = userRepository.getUserByEmail(userToApproveEmail);

        try {
            if (workerThread.canApprove(approver.getUserType(), userToApprove.getUserType())) {
                userToApprove.setApproved(true, approver.getEmail());
                userRepository.updateUser(userToApprove);
                workerThread.sendMessage("APPROVED");

                // Notify the approved user through broadcast
                server.sendBrodcastMessage("USER_APPROVED " + userToApproveEmail);
            } else {
                workerThread.sendMessage("UNAUTHORIZED");
            }

        } catch(CannotWritetoFileException e) {
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }
    }
}
