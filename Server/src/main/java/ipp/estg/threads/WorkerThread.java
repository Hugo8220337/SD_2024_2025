package ipp.estg.threads;

import ipp.estg.Server;
import ipp.estg.commands.*;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.constants.DatabaseFiles;
import ipp.estg.database.models.User;
import ipp.estg.database.models.enums.UserTypes;
import ipp.estg.database.repositories.FileNotificationRepository;
import ipp.estg.database.repositories.FileUserRepository;
import ipp.estg.database.repositories.exceptions.CannotWritetoFileException;
import ipp.estg.database.repositories.interfaces.NotificationRepository;
import ipp.estg.database.repositories.interfaces.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Thread responsável por tratar de um cliente em específico
 */
public class WorkerThread extends Thread {

    /**
     * Database Repositories
     */
    private UserRepository userRepository;
    private NotificationRepository notificationRepository;

    private Server server;
    private Socket clientSocket;

    private BufferedReader in = null;
    private PrintWriter out = null;

    public WorkerThread(Server server, Socket clientSocket) {
        super("server.WorkerThread");
        this.server = server;
        this.clientSocket = clientSocket;

        this.userRepository = new FileUserRepository(DatabaseFiles.USERS_FILE);
        this.notificationRepository = new FileNotificationRepository(DatabaseFiles.NOTIFICATIONS_FILE, userRepository);
        // TODO falta um para os logs
    }

    /**
     * Envia mensagem para o utilizador que está conectado
     *
     * @param message mensagem a ser enviada
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        String username, email, password;
        UserTypes userType;

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String input;
            String[] inputArray;

            input = in.readLine();
            inputArray = input.split(" ");

            switch (inputArray[0]) {
                case CommandsFromClient.LOGIN:
                    LoginCommand loginCommand =
                            new LoginCommand(this, userRepository, inputArray);
                    loginCommand.execute();
                    break;
                case CommandsFromClient.REGISTER:
                    RegisterCommand registerCommand =
                            new RegisterCommand(this, userRepository, inputArray);
                    registerCommand.execute();
                    break;
                case CommandsFromClient.GET_PENDING_APPROVALS:
                    GetPendingApprovalsCommand getPendingApprovalsCommand =
                            new GetPendingApprovalsCommand(this, userRepository, inputArray);
                    getPendingApprovalsCommand.execute();
                    break;
                case CommandsFromClient.APPROVE_USER:
                    ApproveUserCommand approveUserCommand =
                            new ApproveUserCommand(this, server, userRepository, inputArray);
                    approveUserCommand.execute();
                    break;
                case CommandsFromClient.DENY_USER:
                    DenyUserCommand denyUserCommand =
                            new DenyUserCommand(this, server, userRepository, inputArray);
                    denyUserCommand.execute();
                    break;
                default:
                    sendMessage("INVALID_COMMAND");
                    break;
            }

            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO Substiruir isto por um log
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO Substiruir isto por um log
        } finally {
            try {
                server.removeClientFromList(this);
                clientSocket.close();
                in.close();
                out.close();

            } catch (IOException e) {
                System.out.println("Error while closing the server: " + e.getMessage());
            }
        }
    }

    public boolean canApprove(UserTypes approverType, UserTypes userType) {
        return switch (userType) {
            case High -> approverType == UserTypes.High;
            case Medium -> approverType == UserTypes.High || approverType == UserTypes.Medium;
            case Low -> true; // LOW users are auto-approved
        };
    }

    public boolean canApproveUsers(UserTypes userType) {
        return userType == UserTypes.Medium || userType == UserTypes.High;
    }
}
