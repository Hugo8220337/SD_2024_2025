package ipp.estg.commands.activatingEmergencyCommunication;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

public class ActivatingEmergencyCommunicationCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IActivatingEmergencyCommunicationsRepository emergencyCommunicationsRepository;
    private final IUserRepository userRepository;
    private final String[] inputArray;
    private final Server server;

    public ActivatingEmergencyCommunicationCommand(WorkerThread workerThread, IUserRepository userRepository, IActivatingEmergencyCommunicationsRepository emergencyCommunicationsRepository, String[] inputArray, Server server) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyCommunicationsRepository = emergencyCommunicationsRepository;
        this.inputArray = inputArray;
        this.server = server;
    }

    @Override
    public void execute() {
        String requesterIdString = inputArray[1];
        String message = inputArray[2];
        int requesterIdInt = Integer.parseInt(requesterIdString);

        User requester = userRepository.getById(requesterIdInt);

        // Check if user has permission to request
        if (requester.getUserType().equals(UserTypes.Low)) {
            workerThread.sendMessage("ERROR: User does not have permission to request");
        }

        try {
            // Add Activating Emergency Communications request
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.High)) {
                // If requester is high, add approver id
                wasAddSuccessful = emergencyCommunicationsRepository.add(message, requesterIdString);

                // Send Broadcast
                server.sendBrodcastMessage(message);
            }
            else {
                // If requester is medium, add without approver id, so it can be approved later
                wasAddSuccessful = emergencyCommunicationsRepository.add(message);
            }

            // Send response
            workerThread.sendMessage(wasAddSuccessful
                    ? "SUCCESS: Activating Emergency Communications requested"
                    : "ERROR: Activating Emergency Communications request failed");

        } catch (CannotWritetoFileException e) {
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }

    }
}