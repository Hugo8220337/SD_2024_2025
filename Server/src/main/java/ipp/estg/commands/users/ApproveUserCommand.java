package ipp.estg.commands.users;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

/**
 * Command to approve or deny a user in the system
 */
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
     * @param workerThread Worker thread that is executing the command
     * @param userRepository User repository to access the database
     * @param inputArray Input array with the command arguments
     * @param approved True if the user is being approved, false if the user is being denied
     */
    public ApproveUserCommand(WorkerThread workerThread, IUserRepository userRepository, String[] inputArray, boolean approved) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
        this.approved = approved;
    }

    /**
     * Approve a user in the system
     * @param approver User that is approving the user
     * @param userToApprove User to approve
     * @throws CannotWritetoFileException If the user cannot be approved
     */
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

    /**
     * Deny a user in the system
     * @param dennier User that is denying the user
     * @param userToDeny User to deny
     * @throws CannotWritetoFileException If the user cannot be denied
     */
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

    /**
     * Execute the command to approve or deny a user in the system
     */
    @Override
    public void execute() {
        int userThatApprovesId = workerThread.getCurrentUserId();
        if(userThatApprovesId == -1) {
            workerThread.sendMessage("ERROR: User is not logged in, operation denied");
            LOGGER.error("User is not logged in, operation denied");
            return;
        }

        int userToApproveId = Integer.parseInt(inputArray[1]);

        User approver = userRepository.getById(userThatApprovesId);
        User userToApprove = userRepository.getById(userToApproveId);

        try {
            if (approved) {
                approveUser(approver, userToApprove);
            } else {
                denyUser(approver, userToApprove);
            }
        } catch (CannotWritetoFileException e) {
            workerThread.sendMessage("ERROR: Could not approve user");
            LOGGER.error("Error approving user", e);
        }
    }
}
