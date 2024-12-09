package ipp.estg.commands.massEvacuation;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class ApproveMassEvacuationRequestCommand implements ICommand {

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

    /**
     * True if the request is being approved, false if the request is being denied
     */
    private final boolean approved;

    /**
     * Input array with the command arguments
     */
    private final String[] inputArray;

    private static final AppLogger LOGGER = AppLogger.getLogger(ApproveMassEvacuationRequestCommand.class);

    /**
     *
     * @param server
     * @param workerThread
     * @param userRepository
     * @param evacuationRepository
     * @param inputArray
     * @param approved
     */
    public ApproveMassEvacuationRequestCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IMassEvacuationRepository evacuationRepository, String[] inputArray, boolean approved) {
        this.server = server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.evacuationRepository = evacuationRepository;
        this.inputArray = inputArray;
        this.approved = approved;
    }

    private void approveRequest(User approver, MassEvacuation requestToApprove) throws CannotWritetoFileException {
        if (approver.canApproveMassEvacuationRequests()) {
            // aprovar é mudar o id do approver
            requestToApprove.setApproverId(approver.getId());

            // atualizar
            evacuationRepository.update(requestToApprove);

            // enviar mensagem
            workerThread.sendMessage("APPROVED");
            LOGGER.info("Mass evacuation request approved by user id" + approver.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to approve mass evacuation requests");
            LOGGER.error("User by id" + approver.getId() + " does not have permission");
        }
    }

    private void denyRequest(User dennier, MassEvacuation requestToDeny) throws CannotWritetoFileException {
        // Check if user has permission to deny (canAprrove tb serve para Deny, se pode aprovar então também pode negar)
        if (dennier.canApproveMassEvacuationRequests()) {

            // o deny aqui é apagar
            evacuationRepository.remove(requestToDeny.getId());

            workerThread.sendMessage("DENIED");
            LOGGER.info("Mass evacuation request denied by user id" + dennier.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to deny mass evacuation requests");
            LOGGER.error("User by id" + dennier.getId() + " does not have permission");
        }
    }

    @Override
    public void execute() {
        int userThatApprovesId = workerThread.getCurrentUserId();
        if(userThatApprovesId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        int massEvacuationRequestToApproveId = Integer.parseInt(inputArray[1]);

        User approver = userRepository.getById(userThatApprovesId);
        MassEvacuation requestToApprove = evacuationRepository.getById(massEvacuationRequestToApproveId);

        try {
            if (approved) {
                approveRequest(approver, requestToApprove);

                // Send Broadcast when accepted
                server.sendBrodcastMessage(requestToApprove.getMessage());
                LOGGER.info("Broadcast message sent");
            } else {
                denyRequest(approver, requestToApprove);
            }
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Error approving mass evacuation request");
            LOGGER.error("Error approving mass evacuation request", e);
        }
    }
}
