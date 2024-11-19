package ipp.estg.commands;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

public class MassEvacuationCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IMassEvacuationRepository evacuationRepository;
    private final IUserRepository userRepository;
    private final String[] inputArray;

    public MassEvacuationCommand(WorkerThread workerThread, IUserRepository userRepository, IMassEvacuationRepository evacuationRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.evacuationRepository = evacuationRepository;
        this.inputArray = inputArray;
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
            // Add mass evacuation request
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.High)) {
                // If requester is high, add approver id
                wasAddSuccessful = evacuationRepository.add(message, requesterIdString);
            }
            else {
                // If requester is medium, add without approver id, so it can be approved later
                wasAddSuccessful = evacuationRepository.add(message);
            }

            // Send response
            workerThread.sendMessage(wasAddSuccessful
                    ? "SUCCESS: Mass evacuation requested"
                    : "ERROR: Mass evacuation request failed");

        } catch (CannotWritetoFileException e) {
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }
    }
}