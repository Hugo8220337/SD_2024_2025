package ipp.estg.commands.emergencyResourceDistribution;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class AproveEmergencyResourceDistributionCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(AproveEmergencyResourceDistributionCommand.class);

    /**
     * Worker thread that is executing the command
     */
    private final WorkerThread workerThread;

    /**
     *
     */
    private final Server server;

    /**
     * User repository to access the database
     */
    private final IEmergencyResourceDistributionRepository emergencyRepository;

    private final INotificationRepository notificationRepository;

    /**
     * User repository to access the database
     */
    private final IUserRepository userRepository;

    /**
     * True if the request is being approved, false if the request is being denied
     */
    private final boolean approved;

    /**
     * Input array with the command arguments
     */
    private final String[] inputArray;


    public AproveEmergencyResourceDistributionCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository, INotificationRepository notificationRepository, String[] inputArray, boolean approved) {
        this.server = server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.notificationRepository = notificationRepository;
        this.inputArray = inputArray;
        this.approved = approved;
    }

    @Override
    public void execute() {
        int approverId = workerThread.getCurrentUserId();
        if (approverId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        int requestId = Integer.parseInt(inputArray[1]);
        User approver = userRepository.getById(approverId);
        EmergencyResourceDistribution request = emergencyRepository.getById(requestId);

        try {
            if (approved) {
                approveRequest(approver, request);
                server.sendBrodcastMessage(request.getMessage());
                notificationRepository.addToAllUsers("Emergency communications activated: " + request.getMessage());
                LOGGER.info("Request accepted by user " + approver.getId());
            } else {
                denyRequest(approver, request);
                LOGGER.info("Request denied by user " + approver.getId());
            }
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Error processing request");
            LOGGER.error("Error processing request", e);
        }
    }

    private void approveRequest(User approver, EmergencyResourceDistribution request) throws CannotWritetoFileException {
        if (approver.canApproveEmergencyResourceDistributionRequests()) {
            request.setApproverId(approver.getId());
            emergencyRepository.update(request);
            workerThread.sendMessage("APPROVED");
            notificationRepository.add(request.getCreatorId(), "Your request to distribute emergency resources was approved");
            LOGGER.info("Request approved by user " + approver.getId());
        } else {
            workerThread.sendMessage("You don't have permission to approve this request");
            LOGGER.error("User " + approver.getId() + " tried to approve a request without permission");
        }
    }

    private void denyRequest(User dennier, EmergencyResourceDistribution request) throws CannotWritetoFileException {
        if (dennier.canApproveEmergencyResourceDistributionRequests()) {
            emergencyRepository.remove(request.getId()); // deny apaga o pedido da BD
            workerThread.sendMessage("DENIED");
            notificationRepository.add(request.getCreatorId(), "Your request to distribute emergency resources was denied");
            LOGGER.info("Request denied by user " + dennier.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to deny Emergency Resource Distribution requests");
            LOGGER.error("User " + dennier.getId() + " tried to deny a request without permission");
        }
    }


}