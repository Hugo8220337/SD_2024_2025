package ipp.estg.commands.massEvacuation;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class MassEvacuationCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IMassEvacuationRepository evacuationRepository;
    private final IUserRepository userRepository;
    private final String[] inputArray;
    private final Server server;
    private static final AppLogger LOGGER = AppLogger.getLogger(MassEvacuationCommand.class);

    public MassEvacuationCommand(WorkerThread workerThread, IUserRepository userRepository, IMassEvacuationRepository evacuationRepository, String[] inputArray, Server server) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.evacuationRepository = evacuationRepository;
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
        if (requester.getUserType().equals(UserTypes.All) || requester.getUserType().equals(UserTypes.Low)) {
            workerThread.sendMessage("ERROR: User does not have permission to request");
            LOGGER.error("User with id " + requesterIdInt +  " does not have permission to request");
            return;
        }

        try {
            // Add mass evacuation request
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.High)) {
                // If requester is high, add approver id, no need for approval
                wasAddSuccessful = evacuationRepository.add(message, requesterIdString);

                // Send Broadcast
                server.sendBrodcastMessage(message);
                LOGGER.info("Broadcasted mass evacuation request");
            }
            else {
                // If requester is medium, add without approver id, so it can be approved later
                wasAddSuccessful = evacuationRepository.add(message);
                LOGGER.info("Added mass evacuation request");
            }

            String response = wasAddSuccessful
                    ? "SUCCESS: Mass evacuation requested"
                    : "ERROR: Mass evacuation request failed";
            // Send response
            workerThread.sendMessage(response);

            LOGGER.info(response + " by user with id: " + requesterIdString);
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Could not request mass evacuation");
            LOGGER.error("Error adding mass evacuation request", e);
        }
    }
}
