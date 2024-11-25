package ipp.estg.commands.EmergencyResourceDistribution;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

public class EmergencyResourceDistributionCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IEmergencyResourceDistributionRepository emergencyRepository;
    private final IUserRepository userRepository;
    private final String[] inputArray;
    private final Server server;

    public EmergencyResourceDistributionCommand(WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository, String[] inputArray, Server server) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.inputArray = inputArray;
        this.server = server;
    }

    @Override
    public void execute() {
        String requesterIdString = inputArray[1];
        String message = inputArray[2];
        int requesterIdInt = Integer.parseInt(requesterIdString);

        User requester = userRepository.getById(requesterIdInt);

        try {
            // Add Emergency Resource Distribution request
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.Low) ) {
                // If requester is medium, add without approver id, so it can be approved later
                wasAddSuccessful = emergencyRepository.add(message);

            } else {
                // If requester is high, add approver id
                wasAddSuccessful = emergencyRepository.add(message, requesterIdString);

                // Send Broadcast
                server.sendBrodcastMessage(message);
            }

            // Send response
            workerThread.sendMessage(wasAddSuccessful
                    ? "SUCCESS: Emergency Resource Distribution requested"
                    : "ERROR: Emergency Resource Distribution request failed");
        } catch (CannotWritetoFileException e) {
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }

    }
}
