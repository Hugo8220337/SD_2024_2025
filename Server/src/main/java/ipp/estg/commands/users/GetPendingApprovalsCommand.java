package ipp.estg.commands.users;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.dto.UserResponseDto;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

public class GetPendingApprovalsCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(GetPendingApprovalsCommand.class);
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;

    public GetPendingApprovalsCommand(WorkerThread workerThread, IUserRepository userRepository) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
    }

    /**
     * Get pending users based on the user type
     * @param userType
     * @return List of pending users
     */
    private List<User> getPendingUsers(UserTypes userType) {
        List<User> pendingUsers = new ArrayList<>();

        switch (userType) {
            case High:
                // High Users can get medium and high users
                pendingUsers = userRepository.getPendingUsers(UserTypes.High);
                pendingUsers.addAll(userRepository.getPendingUsers(UserTypes.Medium));
                pendingUsers.addAll(userRepository.getPendingUsers(UserTypes.Low));
                break;
            case Medium:
                // Medium users can only get medium pending users
                pendingUsers = userRepository.getPendingUsers(UserTypes.Medium);
                pendingUsers.addAll(userRepository.getPendingUsers(UserTypes.Low));
                break;
        }

        return pendingUsers;
    }

    /**
     * Execute the command
     */
    @Override
    public void execute() {
        int userId = workerThread.getCurrentUserId();
        if(userId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        User requestingUser = userRepository.getById(userId);

        // Check if user has permission to approve
        if (requestingUser == null || !requestingUser.canApproveUsers(requestingUser.getUserType())) {
            workerThread.sendMessage("ERROR: User does not have permission to approve users");
            LOGGER.error("User " + userId + " does not have permission to approve users");
            return;
        }

        try {
            // Mount Response with pending users
            JsonConverter converter = new JsonConverter();
            List<User> pendingUsers = getPendingUsers(requestingUser.getUserType());

            // Convert to DTO
            List<UserResponseDto> pendingUsersDtos = UserResponseDto.fromUserToUserResponseDto(pendingUsers);
            String json = converter.toJson(pendingUsersDtos);

            // Send pending users to the client
            workerThread.sendMessage(json);
            LOGGER.info("Pending users sent to user " + userId);
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get pending users");
            LOGGER.error("Could not get pending users: " + e.getMessage());
        }
    }
}
