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

/**
 * Command to approve or deny emergency resource distribution requests.
 * The command allows an approver to approve or deny requests and notify relevant users.
 */
public class ApproveEmergencyResourceDistributionCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(ApproveEmergencyResourceDistributionCommand.class);

    /**
     * Worker thread that is executing the command
     */
    private final WorkerThread workerThread;

    /**
     * Server object to send broadcast messages.
     */
    private final Server server;

    /**
     * User repository to access the database
     */
    private final IEmergencyResourceDistributionRepository emergencyRepository;

    /**
     * Repository for notifications.
     */
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

    /**
     * Constructor to initialize the command with necessary dependencies.
     *
     * @param server the server to send broadcast messages
     * @param workerThread the worker thread executing the command
     * @param userRepository repository to access user information
     * @param emergencyRepository repository to access emergency resource distribution requests
     * @param notificationRepository repository for notifications
     * @param inputArray input arguments passed with the command
     * @param approved boolean flag indicating if the request is being approved
     */
    public ApproveEmergencyResourceDistributionCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository, INotificationRepository notificationRepository, String[] inputArray, boolean approved) {
        this.server = server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.notificationRepository = notificationRepository;
        this.inputArray = inputArray;
        this.approved = approved;
    }

    /**
     * Executes the command to approve or deny an emergency resource distribution request.
     * The approver's permissions are checked before processing the request.
     * If approved, the request is updated and notifications are sent.
     * If denied, the request is removed and notifications are sent.
     *
     * If the user is not logged in or if there is an error in processing the request, an error message is sent.
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

    /**
     * Approves an emergency resource distribution request and updates the status.
     * The approver must have permission to approve such requests.
     * Sends notifications to the request creator and to all users.
     *
     * @param approver the user approving the request
     * @param request the emergency resource distribution request to approve
     * @throws CannotWritetoFileException if there is an error writing to the repository
     */
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

    /**
     * Denies an emergency resource distribution request and removes it from the repository.
     * The user denying the request must have permission to approve such requests.
     * Sends a notification to the request creator.
     *
     * @param dennier the user denying the request
     * @param request the emergency resource distribution request to deny
     * @throws CannotWritetoFileException if there is an error writing to the repository
     */
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