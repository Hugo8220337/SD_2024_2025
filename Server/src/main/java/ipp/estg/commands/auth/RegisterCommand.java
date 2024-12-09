package ipp.estg.commands.auth;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;

public class RegisterCommand implements ICommand {

    private final WorkerThread workerThread;
    private final IUserRepository IUserRepository;
    private final String[] inputArray;
    private static final AppLogger LOGGER = AppLogger.getLogger(RegisterCommand.class);

    public RegisterCommand(WorkerThread workerThread, IUserRepository IUserRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.IUserRepository = IUserRepository;
        this.inputArray = inputArray;
    }

    @Override
    public void execute() {
        String username = inputArray[1];
        String email = inputArray[2];
        String password = inputArray[3];
        UserTypes userType = UserTypes.getUserType(inputArray[4]);

        try {
            // insert user in the database
            boolean addedUser = IUserRepository.add(username, email, password, userType);
            if (addedUser) {
                workerThread.sendMessage("SUCCESS");
                LOGGER.info("User " + username + " registered successfully");
            } else {
                workerThread.sendMessage("ERROR: User already exists");
                LOGGER.error("User " + username + " already exists");
            }
        } catch (CannotWritetoFileException cwtfe) {
            LOGGER.error("Error while writing to file: " + cwtfe.getMessage());
            workerThread.sendMessage("ERROR: Could not register user");
        }
    }
}
