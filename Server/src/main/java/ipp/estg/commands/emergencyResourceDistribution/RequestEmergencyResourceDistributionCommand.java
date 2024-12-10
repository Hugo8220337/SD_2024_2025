package ipp.estg.commands.emergencyResourceDistribution;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class RequestEmergencyResourceDistributionCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IEmergencyResourceDistributionRepository emergencyRepository;
    private final IUserRepository userRepository;
    private final String[] inputArray;
    private final Server server;
    private static final AppLogger LOGGER = AppLogger.getLogger(RequestEmergencyResourceDistributionCommand.class);

    public RequestEmergencyResourceDistributionCommand(WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository, String[] inputArray, Server server) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.inputArray = inputArray;
        this.server = server;
    }

    @Override
    public void execute() {
        int requesterId = workerThread.getCurrentUserId();
        String message = inputArray[1];
        User requester = userRepository.getById(requesterId);

        try {
            // Add Emergency Resource Distribution request
            boolean wasAddSuccessful;
            if (requester.getUserType().equals(UserTypes.All) ) {
                // If requester is low, do not add approver id, it should wait for approval
                wasAddSuccessful = emergencyRepository.add(message, requesterId);
                LOGGER.info("Emergency Resource Distribution requested by low user by id" + requesterId);
            } else {
                // If requester is Low or Higher add approver id and send the boradcast, no need for approval
                wasAddSuccessful = emergencyRepository.add(message, requesterId, requesterId);
                server.sendBrodcastMessage(message);
                LOGGER.info("Emergency Resource Distribution requested by high user with id: " + requesterId);
            }

            String response = wasAddSuccessful
                    ? "SUCCESS: Emergency Resource Distribution requested"
                    : "ERROR: Emergency Resource Distribution request failed";
            workerThread.sendMessage(response);
            LOGGER.info(response + " by user with id: " + requesterId);
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Could not request Emergency Resource Distribution");
            LOGGER.error("Could not request Emergency Resource Distribution: " + e.getMessage());
        }

    }
}
