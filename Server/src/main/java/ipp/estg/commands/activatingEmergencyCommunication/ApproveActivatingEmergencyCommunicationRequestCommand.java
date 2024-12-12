package ipp.estg.commands.activatingEmergencyCommunication;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.ActivatingEmergencyCommunications;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

/**
 * Command to approve or deny an Activating Emergency Communication request.
 * This command handles the logic of processing a request and sends notifications based on the outcome.
 */
public class ApproveActivatingEmergencyCommunicationRequestCommand implements ICommand {
    /** Logger instance for logging command operations. */
    private static final AppLogger LOGGER = AppLogger.getLogger(ApproveActivatingEmergencyCommunicationRequestCommand.class);

    /** Server instance for sending broadcast messages. */
    private final Server server;

    /** WorkerThread instance representing the current thread processing the request. */
    private final WorkerThread workerThread;

    /** Repository for handling Activating Emergency Communications requests. */
    private final IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository;

    /** Repository for handling user-related operations. */
    private final IUserRepository userRepository;

    /** Repository for handling notification-related operations. */
    private final INotificationRepository notificationRepository;

    /** Indicates whether the request should be approved or denied. */
    private final boolean approved;

    /** Input array containing the command details. */
    private final String[] inputArray;

    /**
     * Constructs a new ApproveActivatingEmergencyCommunicationRequestCommand.
     *
     * @param server The server instance for broadcasting messages.
     * @param workerThread The current worker thread handling the request.
     * @param userRepository Repository for user operations.
     * @param activatingEmergencyCommunicationsRepository Repository for emergency communication requests.
     * @param notificationRepository Repository for notifications.
     * @param inputArray Input array with command arguments.
     * @param approved Boolean flag indicating if the request is approved or denied.
     */
    public ApproveActivatingEmergencyCommunicationRequestCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository, INotificationRepository notificationRepository, String[] inputArray, boolean approved) {
        this.server = server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.activatingEmergencyCommunicationsRepository = activatingEmergencyCommunicationsRepository;
        this.notificationRepository = notificationRepository;
        this.inputArray = inputArray;
        this.approved = approved;
    }

    /**
     * Executes the command to either approve or deny the Activating Emergency Communication request.
     * Sends appropriate notifications and handles repository updates.
     */
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
        ActivatingEmergencyCommunications request = activatingEmergencyCommunicationsRepository.getById(requestId);

        try {
            if (approved) {
                approveRequest(approver, request);
                server.sendBrodcastMessage(request.getMessage());
                notificationRepository.addToAllUsers("Emergency communications activated: " + request.getMessage());
                LOGGER.info("Broadcasted message: " + request.getMessage());
            } else {
                denyRequest(approver, request);
                LOGGER.info("Denied request with id: " + request.getId());
            }
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Error processing request");
            LOGGER.error("Error processing request", e);
        }
    }

    /**
     * Approves the given Activating Emergency Communication request.
     * Updates the repository and sends notifications.
     *
     * @param approver The user approving the request.
     * @param request The request to be approved.
     * @throws CannotWritetoFileException If an error occurs while updating the repository.
     */
    private void approveRequest(User approver, ActivatingEmergencyCommunications request) throws CannotWritetoFileException {
        if (approver.canApproveEmergencyCommunicationsRequests()) {
            request.setApproverId(approver.getId());
            activatingEmergencyCommunicationsRepository.update(request);
            workerThread.sendMessage("APPROVED");
            notificationRepository.add(request.getCreatorId(), "Your request to activate emergency communications was approved");
            LOGGER.info("Approved request with id: " + request.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to approve Activating Emergency Communications requests");
            LOGGER.error("User with id " + approver.getId() + " does not have permission to approve Activating Emergency Communications requests");
        }
    }

    /**
     * Denies the given Activating Emergency Communication request.
     * Removes the request from the repository and sends notifications.
     *
     * @param approver The user denying the request.
     * @param request The request to be denied.
     * @throws CannotWritetoFileException If an error occurs while removing the request from the repository.
     */
    private void denyRequest(User approver, ActivatingEmergencyCommunications request) throws CannotWritetoFileException {
        if (approver.canApproveEmergencyCommunicationsRequests()) {
            activatingEmergencyCommunicationsRepository.remove(request.getId()); // Deny apaga o pedido da BD
            workerThread.sendMessage("DENIED");
            notificationRepository.add(request.getCreatorId(), "Your request to activate emergency communications was denied");
            LOGGER.info("Denied request with id: " + request.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to deny Activating Emergency Communications requests");
            LOGGER.error("User with id " + approver.getId() + " does not have permission to deny Activating Emergency Communications requests");
        }
    }
}
