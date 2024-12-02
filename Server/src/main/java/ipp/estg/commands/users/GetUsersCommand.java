package ipp.estg.commands.users;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.dto.response.UserResponseDto;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.List;

public class GetUsersCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(GetPendingApprovalsCommand.class);
    private final WorkerThread workerThread;
    private final IUserRepository userRepository;

    public GetUsersCommand(WorkerThread workerThread, IUserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
    }

    @Override
    public void execute() {

        try {
            List<User> users = userRepository.getAll();

            List<UserResponseDto> userResponseDtos = UserResponseDto.fromUserToUserResponseDto(users);

            JsonConverter converter = new JsonConverter();
            String json = converter.toJson(userResponseDtos);
            workerThread.sendMessage(json);

            LOGGER.info("Users sent to client");
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Unable to get users");
            LOGGER.error("Unable to get users", e);
        }
    }
}
