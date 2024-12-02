package ipp.estg.commands.userApproval;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class ApproveUserCommand implements ICommand {

    /**
     * Worker thread that is executing the command
     */
    private final WorkerThread workerThread;

    /**
     * User repository to access the database
     */
    private final IUserRepository userRepository;

    /**
     * True if the user is being approved, false if the user is being denied
     */
    private final boolean approved;

    /**
     * Input array with the command arguments
     */
    private final String[] inputArray;

    /**
     * Logger for the class ApproveUserCommand
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(ApproveUserCommand.class);

    /**
     *
     * @param workerThread
     * @param userRepository
     * @param inputArray
     * @param approved
     */
    public ApproveUserCommand(WorkerThread workerThread, IUserRepository userRepository, String[] inputArray, boolean approved) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
        this.approved = approved;
    }

    private void approveUser(User approver, User userToApprove) throws CannotWritetoFileException {
        if (approver.canApproveUsers(userToApprove.getUserType())) {
            userToApprove.setApproved(true, approver.getId());
            userRepository.update(userToApprove);
            workerThread.sendMessage("APPROVED");
            LOGGER.info("User " + userToApprove.getId() + " approved by user " + approver.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to approve users");
            LOGGER.error("User " + approver.getId() + " does not have permission to approve users");
        }
    }

    private void denyUser(User dennier, User userToDeny) throws CannotWritetoFileException {
        // Check if user has permission to deny (canAprrove tb serve para Deny, se pode aprovar então também pode negar)
        if (dennier.canApproveUsers(userToDeny.getUserType())) {

            // o deny aqui é apagar
            userRepository.remove(userToDeny.getId());

            workerThread.sendMessage("DENIED");
            LOGGER.info("User " + userToDeny.getId() + " denied by user " + dennier.getId());
        } else {
            workerThread.sendMessage("ERROR: User does not have permission to deny users");
            LOGGER.error("User " + dennier.getId() + " does not have permission to deny users");
        }
    }

    @Override
    public void execute() {
        int userThatApprovesId = Integer.parseInt(inputArray[1]);
        int userToApproveId = Integer.parseInt(inputArray[2]);

        User approver = userRepository.getById(userThatApprovesId);
        User userToApprove = userRepository.getById(userToApproveId);

        try {
            if (approved) {
                approveUser(approver, userToApprove);
            } else {
                denyUser(approver, userToApprove);
            }
        } catch (CannotWritetoFileException e) {
            LOGGER.error("Error approving user", e);
            throw new RuntimeException("Error approving user", e); // TODO retirar isto
        }
    }
}
