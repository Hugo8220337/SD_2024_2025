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
        int approverId = Integer.parseInt(inputArray[1]);
        int userToApproveId = Integer.parseInt(inputArray[2]);

        User approver = userRepository.getUserById(approverId);
        User userToApprove = userRepository.getUserById(userToApproveId);

        try {
            if (workerThread.canApprove(approver.getUserType(), userToApprove.getUserType())) {
                userToApprove.setApproved(true, approver.getId());
                userRepository.updateUser(userToApprove);
                workerThread.sendMessage("APPROVED");

                // Notify the approved user through broadcast
                server.sendBrodcastMessage("USER_APPROVED " + userToApproveId); // TODO ACHO que broadcast aqui é parvo, isto é tipo festinha, falta só os outros mandarem broadcast a dar os parabéns
            } else {
                workerThread.sendMessage("ERROR: User does not have permission to approve users");
            }

        } catch(CannotWritetoFileException e) {
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }
    }
}
