package ipp.estg.commands.massEvacuation;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class ApproveMassEvacuationRequestCommand implements ICommand {

    private static final AppLogger LOGGER = AppLogger.getLogger(ApproveMassEvacuationRequestCommand.class);

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
    private final IMassEvacuationRepository evacuationRepository;

    /**
     * User repository to access the database
     */
    private final IUserRepository userRepository;

    private final INotificationRepository notificationRepository;

    /**
     * True if the request is being approved, false if the request is being denied
     */
    private final boolean approved;

    /**
     * Input array with the command arguments
     */
    private final String[] inputArray;



    public ApproveMassEvacuationRequestCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IMassEvacuationRepository evacuationRepository, INotificationRepository notificationRepository, String[] inputArray, boolean approved) {
        this.server = server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.evacuationRepository = evacuationRepository;
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
        MassEvacuation request = evacuationRepository.getById(requestId);

        try {
            if (approved) {
                approveRequest(approver, request);
                server.sendBrodcastMessage(request.getMessage());
                notificationRepository.addToAllUsers("Emergency communications activated: " + request.getMessage());
                LOGGER.info("Broadcast message sent");
            } else {
                denyRequest(approver, request);
            }
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Error processing mass evacuation request");
            LOGGER.error("Error processing mass evacuation request", e);
        }
    }

    private void approveRequest(User approver, MassEvacuation request) throws CannotWritetoFileException {
        if (approver.canApproveMassEvacuationRequests()) {
            request.setApproverId(approver.getId());
            evacuationRepository.update(request);
            workerThread.sendMessage("APPROVED");
            notificationRepository.add(request.getCreatorId(), "Your request to evacuate the area was approved");
            LOGGER.info("Mass evacuation request approved by user id " + approver.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to approve mass evacuation requests");
            LOGGER.error("User id " + approver.getId() + " does not have permission");
        }
    }

    private void denyRequest(User dennier, MassEvacuation request) throws CannotWritetoFileException {
        if (dennier.canApproveMassEvacuationRequests()) {
            evacuationRepository.remove(request.getId());
            workerThread.sendMessage("DENIED");
            notificationRepository.add(request.getCreatorId(), "Your request to evacuate the area was denied");
            LOGGER.info("Mass evacuation request denied by user id " + dennier.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to deny mass evacuation requests");
            LOGGER.error("User id " + dennier.getId() + " does not have permission");
        }
    }
}
