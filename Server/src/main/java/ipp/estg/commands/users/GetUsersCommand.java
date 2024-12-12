package ipp.estg.commands.users;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.dto.UserResponseDto;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.List;

/**
 * Command to get all users from the database
 */
public class GetUsersCommand implements ICommand {

    /**
     * Logger for the GetUsersCommand class
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(GetPendingApprovalsCommand.class);

    /**
     * WorkerThread that will receive the response
     */
    private final WorkerThread workerThread;

    /**
     * Repository to access the database
     */
    private final IUserRepository userRepository;

    /**
     * Constructor for the GetUsersCommand
     * @param workerThread WorkerThread that will receive the response
     * @param userRepository Repository to access the database
     * @param inputArray Array with the input parameters
     */
    public GetUsersCommand(WorkerThread workerThread, IUserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
    }

    /**
     * Execute the command
     */
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
