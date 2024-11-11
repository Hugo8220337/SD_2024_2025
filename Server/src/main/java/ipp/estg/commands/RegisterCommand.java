package ipp.estg.commands;

import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.UserRepository;
import ipp.estg.threads.WorkerThread;

public class RegisterCommand implements Command {

    private final WorkerThread workerThread;
    private final UserRepository userRepository;
    private final String[] inputArray;

    public RegisterCommand(WorkerThread workerThread, UserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
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
            boolean addedUser = userRepository.addUser(username, email, password, userType);
            if (addedUser) {
                if (userType == UserTypes.Low) {
                    workerThread.sendMessage("SUCCESS");
                } else {
                    workerThread.sendMessage("PENDING_APPROVAL");
                }
            } else {
                workerThread.sendMessage("FAILIURE");
            }
        } catch (CannotWritetoFileException cwtfe) {
            throw new RuntimeException("Error while writing to file: " + cwtfe.getMessage()); // TODO retirar porque o server não pode parar, substituir por um log
        }
    }
}
