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

public class ApproveActivatingEmergencyCommunicationRequestCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(ApproveActivatingEmergencyCommunicationRequestCommand.class);

    private final Server server;
    private final WorkerThread workerThread;
    private final IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository;
    private final IUserRepository userRepository;
    private final INotificationRepository notificationRepository;
    private final boolean approved;
    private final String[] inputArray;

    public ApproveActivatingEmergencyCommunicationRequestCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository, INotificationRepository notificationRepository, String[] inputArray, boolean approved) {
        this.server = server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.activatingEmergencyCommunicationsRepository = activatingEmergencyCommunicationsRepository;
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
