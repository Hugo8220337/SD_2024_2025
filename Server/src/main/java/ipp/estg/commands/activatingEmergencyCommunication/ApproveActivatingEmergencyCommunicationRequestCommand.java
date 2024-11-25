package ipp.estg.commands.activatingEmergencyCommunication;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.ActivatingEmergencyCommunications;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IActivatingEmergencyCommunicationsRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

public class ApproveActivatingEmergencyCommunicationRequestCommand implements ICommand {

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
    private final IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository;


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

    public ApproveActivatingEmergencyCommunicationRequestCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository, String[] inputArray, boolean approved) {
        this.server = server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.activatingEmergencyCommunicationsRepository = activatingEmergencyCommunicationsRepository;
        this.inputArray = inputArray;
        this.approved = approved;
    }

    private void approveRequest(User approver, ActivatingEmergencyCommunications requestToApprove) throws CannotWritetoFileException {
        if (approver.canApproveEmergencyCommunicationsRequests()) {
            // aprovar é mudar o id do approver
            requestToApprove.setApproverId(approver.getId());

            // atualizar
            activatingEmergencyCommunicationsRepository.update(requestToApprove);

            // enviar mensagem
            workerThread.sendMessage("APPROVED");
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to approve Activating Emergency Communications requests");
        }
    }

    private void denyRequest(User dennier, ActivatingEmergencyCommunications requestToDeny) throws CannotWritetoFileException {
        // Check if user has permission to deny (canAprrove tb serve para Deny, se pode aprovar então também pode negar)
        if (dennier.canApproveEmergencyCommunicationsRequests()) {

            // o deny aqui é apagar
            activatingEmergencyCommunicationsRepository.remove(requestToDeny.getId());

            workerThread.sendMessage("DENIED");
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to deny Activating Emergency Communications requests");
        }
    }

    @Override
    public void execute() {
        int userThatApprovesId = Integer.parseInt(inputArray[1]);
        int activatingEmergencyCommunicationsRequestToApproveId = Integer.parseInt(inputArray[2]);

        User approver = userRepository.getById(userThatApprovesId);
        ActivatingEmergencyCommunications requestToApprove = activatingEmergencyCommunicationsRepository.getById(activatingEmergencyCommunicationsRequestToApproveId);

        try {
            if (approved) {
                approveRequest(approver, requestToApprove);

                // Send Broadcast when accepted
                server.sendBrodcastMessage(requestToApprove.getMessage());
            } else {
                denyRequest(approver, requestToApprove);
            }
        } catch (CannotWritetoFileException e) {
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }
    }
}
