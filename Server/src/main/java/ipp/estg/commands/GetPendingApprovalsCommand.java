package ipp.estg.commands;

import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.interfaces.UserRepository;
import ipp.estg.dto.response.UserResponseDto;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.JsonConverter;

import java.util.List;

public class GetPendingApprovalsCommand implements Command {
    private final WorkerThread workerThread;
    private final UserRepository userRepository;
    private final String[] inputArray;

    public GetPendingApprovalsCommand(WorkerThread workerThread, UserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
    }

    @Override
    public void execute() {
//        String userEmail = inputArray[1];
//
//        // Check if user has permission to approve
//        User requestingUser = userRepository.getUserByEmail(userEmail);
//        if (requestingUser == null || !workerThread.canApproveUsers(requestingUser.getUserType())) {
//            workerThread.sendMessage("UNAUTHORIZED");
//            return;
//        }

        // Mount Response with pending users
        JsonConverter converter = new JsonConverter();
//        List<User> pendingUsers = userRepository.getPendingUsers(requestingUser.getUserType());

        List<User> pendingUsers = userRepository.getPendingUsers(UserTypes.Medium);

        List<UserResponseDto> pendingUsersDtos = UserResponseDto.fromUserToUserResponseDto(pendingUsers);
        String json = converter.toJson(pendingUsersDtos);

        // Send Pedning users to the client
        workerThread.sendMessage(json);
    }
}
