package ipp.estg.commands;

import ipp.estg.database.models.MassEvacuation;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IMassEvacuationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

public class ApproveMassEvacuationRequestCommand implements ICommand {

    /**
     * Worker thread that is executing the command
     */
    private final WorkerThread workerThread;

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

    public ApproveMassEvacuationRequestCommand(WorkerThread workerThread, IUserRepository userRepository, IMassEvacuationRepository evacuationRepository, String[] inputArray, boolean approved) {
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
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to approve mass evacuation requests");
        }
    }

    private void denyRequest(User dennier, MassEvacuation requestToDeny) throws CannotWritetoFileException {
        // Check if user has permission to deny (canAprrove tb serve para Deny, se pode aprovar então também pode negar)
        if (dennier.canApproveMassEvacuationRequests()) {

            // o deny aqui é apagar
            evacuationRepository.remove(requestToDeny.getId());

            workerThread.sendMessage("DENIED");
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to deny mass evacuation requests");
        }
    }

    @Override
    public void execute() {
        int userThatApprovesId = Integer.parseInt(inputArray[1]);
        int massEvacuationRequestToApproveId = Integer.parseInt(inputArray[2]);

        User approver = userRepository.getById(userThatApprovesId);
        MassEvacuation requestToApprove = evacuationRepository.getById(massEvacuationRequestToApproveId);

        try {
            if (approved) {
                approveRequest(approver, requestToApprove);
            } else {
                denyRequest(approver, requestToApprove);
            }
        } catch (CannotWritetoFileException e) {
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }
    }
}
