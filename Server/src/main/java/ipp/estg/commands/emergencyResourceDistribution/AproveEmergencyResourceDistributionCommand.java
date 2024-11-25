package ipp.estg.commands.emergencyResourceDistribution;

import ipp.estg.Server;
import ipp.estg.commands.ICommand;
import ipp.estg.database.models.EmergencyResourceDistribution;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IEmergencyResourceDistributionRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

public class AproveEmergencyResourceDistributionCommand implements ICommand {

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

    public AproveEmergencyResourceDistributionCommand(Server server, WorkerThread workerThread, IUserRepository userRepository, IEmergencyResourceDistributionRepository emergencyRepository, String[] inputArray, boolean approved) {
        this.server = server;
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.inputArray = inputArray;
        this.approved = approved;
    }

    private void approveRequest(User approver, EmergencyResourceDistribution requestToApprove) throws CannotWritetoFileException {
        if (approver.canApproveEmergencyResourceDistributionRequests()) {
            // aprovar é mudar o id do approver
            requestToApprove.setApproverId(approver.getId());

            // atualizar
            emergencyRepository.update(requestToApprove);
        } else {
            workerThread.sendMessage("You don't have permission to approve this request");
        }
    }

    private void denyRequest(User dennier, EmergencyResourceDistribution requestToDeny) throws CannotWritetoFileException {
        if (dennier.canApproveEmergencyResourceDistributionRequests()) {
            // o deny aqui é apagar
            emergencyRepository.remove(requestToDeny.getId());

            workerThread.sendMessage("DENIED");
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to deny Emergency Resource Distribution requests");
        }
    }

    @Override
    public void execute() {
        int userThatApprovesId = Integer.parseInt(inputArray[1]);
        int EmergencyResourceDistributionRequestToApproveId = Integer.parseInt(inputArray[2]);

        User approver = userRepository.getById(userThatApprovesId);
        EmergencyResourceDistribution requestToApprove = emergencyRepository.getById(EmergencyResourceDistributionRequestToApproveId);

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
