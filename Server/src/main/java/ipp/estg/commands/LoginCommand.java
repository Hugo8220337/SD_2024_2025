package ipp.estg.commands;

import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.UserRepository;
import ipp.estg.threads.WorkerThread;

public class LoginCommand implements Command {

    private final WorkerThread workerThread;
    private final UserRepository userRepository;
    private final String[] inputArray;

    public LoginCommand(WorkerThread workerThread, UserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
    }

    @Override
    public void execute() {
        String email = inputArray[1];
        String password = inputArray[2];

        User user = userRepository.login(email, password);

        if(user == null) {
            workerThread.sendMessage("FAILIURE");
            return;
        }

        if(!user.isApproved()) {
            workerThread.sendMessage("PENDING_APPROVAL");
            return;
        }

        workerThread.sendMessage("SUCCESS");
    }
}
