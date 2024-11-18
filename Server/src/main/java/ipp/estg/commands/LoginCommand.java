package ipp.estg.commands;

import ipp.estg.database.models.User;
import ipp.estg.database.repositories.interfaces.UserRepository;
import ipp.estg.dto.response.LoginResponseDto;
import ipp.estg.dto.response.UserResponseDto;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

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


        JsonConverter converter = new JsonConverter();
        if (user == null) {
            workerThread.sendMessage("ERROR: Invalid email or password");
            return;
        }

        if (!user.isApproved()) {
            workerThread.sendMessage("ERROR: User not approved");
            return;
        }

        // Mount response
        String jsonResponse = converter.toJson(new LoginResponseDto(
                Integer.toString(user.getId()),
                user.getUserType().toString()
        ));

        // Send Response
        workerThread.sendMessage(jsonResponse);
    }
}
