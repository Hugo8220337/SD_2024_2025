package ipp.estg.commands.auth;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.dto.response.LoginResponseDto;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.JsonConverter;

public class LoginICommand implements ICommand {

    private final WorkerThread workerThread;
    private final IUserRepository userRepository;
    private final String[] inputArray;

    public LoginICommand(WorkerThread workerThread, IUserRepository userRepository, String[] inputArray) {
        this.workerThread = workerThread;
        this.userRepository = userRepository;
        this.inputArray = inputArray;
    }

    @Override
    public void execute() {
        String email = inputArray[1];
        String password = inputArray[2];

        User user = userRepository.login(email, password);


        if (user == null) {
            workerThread.sendMessage("ERROR: Invalid email or password");
            return;
        }

        if (!user.isApproved()) {
            workerThread.sendMessage("ERROR: User not approved");
            return;
        }

        // Mount response
        JsonConverter converter = new JsonConverter();
        String jsonResponse = converter.toJson(new LoginResponseDto(
                Integer.toString(user.getId()),
                user.getUserType().toString()
        ));

        // Send Response
        workerThread.sendMessage(jsonResponse);
    }
}
