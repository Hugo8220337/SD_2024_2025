package ipp.estg.commands;

import ipp.estg.Server;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.UserRepository;
import ipp.estg.threads.WorkerThread;

public class DenyUserCommand implements Command {

    private final WorkerThread workerThread;
    private final Server server;
    private final UserRepository userRepository;
    private final String[] inputArray;

    public DenyUserCommand(WorkerThread workerThread, Server server, UserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.server = server;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
    }

    @Override
    public void execute() {
        int denierId = Integer.parseInt(inputArray[1]);
        int userToDenyId = Integer.parseInt(inputArray[2]);

        User approver = userRepository.getUserById(denierId);
        User userToDeny = userRepository.getUserById(userToDenyId);

        try {
            // Check if user has permission to deny (canAprrove tb serve para Deny, se pode aprovar então também pode negar)
            if (workerThread.canApprove(approver.getUserType(), userToDeny.getUserType())) {

                // o deny aqui é apagar
                userRepository.removeUser(userToDenyId);

                workerThread.sendMessage("DENIED");
            } else {
                workerThread.sendMessage("ERROR: User does not have permission to deny users");
            }

        } catch(CannotWritetoFileException e) {
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }
    }
}
