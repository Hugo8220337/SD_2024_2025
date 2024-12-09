package ipp.estg.commands.activatingEmergencyCommunication;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class ActivatingEmergencyCommunicationCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(ActivatingEmergencyCommunicationCommand.class);

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
        int requesterId = workerThread.getCurrentUserId();
        if (requesterId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        String message = inputArray[1];
        LOGGER.info("ActivatingEmergencyCommunicationCommand started for user with id: " + requesterId);

        User requester = userRepository.getById(requesterId);
        if (requester.getUserType().equals(UserTypes.All)) {
            workerThread.sendMessage("ERROR: User does not have permission to request");
            LOGGER.error("User with id " + requesterId + " does not have permission to request");
            return;
        }

        try {
            // Add Activating Emergency Communications request
            // high and medium users can activate without approval
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.High) || requester.getUserType().equals(UserTypes.Medium)) {
                // If requester is high, add approver id
                wasAddSuccessful = emergencyCommunicationsRepository.add(message, requesterId);

                server.sendBrodcastMessage(message);
                LOGGER.info("Broadcasted message: " + message);
            }
            else {
                // If requester is medium, add without approver id, so it can be approved later
                wasAddSuccessful = emergencyCommunicationsRepository.add(message, requesterId);
                LOGGER.info("Added message to be approved later: " + message);
            }

            String response = wasAddSuccessful
                    ? "SUCCESS: Activating Emergency Communications requested"
                    : "ERROR: Activating Emergency Communications request failed";
            workerThread.sendMessage(response);
            LOGGER.info(response + " user with id: " + requesterId);
        } catch (CannotWritetoFileException e) {
            LOGGER.error("Error approving user", e);
            workerThread.sendMessage("ERROR: Activating Emergency Communications request failed");
        }

    }
}
