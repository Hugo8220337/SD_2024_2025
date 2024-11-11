package ipp.estg.threads;

import ipp.estg.Server;
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
                    email = inputArray[1];
                    password = inputArray[2];

                    User user = userRepository.login(email, password);

                    if(user == null) {
                        sendMessage("FAILIURE");
                        break;
                    }

                    if(!user.isApproved()) {
                        sendMessage("PENDING_APPROVAL");
                        break;
                    }

                    sendMessage("SUCCESS");
                    break;
                case CommandsFromClient.REGISTER:
                    username = inputArray[1];
                    email = inputArray[2];
                    password = inputArray[3];
                    userType = UserTypes.getUserType(inputArray[4]);

                    // insert user in the database
                    boolean addedUser = userRepository.addUser(username, email, password, userType);
                    if (addedUser) {
                        if (userType == UserTypes.Low) {
                            sendMessage("SUCCESS");
                        } else {
                            sendMessage("PENDING_APPROVAL");
                        }
                    } else {
                        sendMessage("FAILIURE");
                    }

                    break;
                case CommandsFromClient.GET_PENDING_APPROVALS:
                    // Check if user has permission to approve
                    User requestingUser = userRepository.getUserByEmail(inputArray[1]);
                    if (requestingUser == null || !canApproveUsers(requestingUser.getUserType())) {
                        sendMessage("UNAUTHORIZED");
                        break;
                    }

                    List<User> pendingUsers = userRepository.getPendingUsers(requestingUser.getUserType());
                    StringBuilder response = new StringBuilder("PENDING_USERS ");
                    for (User pendingUser : pendingUsers) {
                        response.append(pendingUser.getEmail()).append(",");
                    }
                    sendMessage(response.toString());
                    break;

                case CommandsFromClient.APPROVE_USER:
                    String approverEmail = inputArray[1];
                    String userToApproveEmail = inputArray[2];

                    User approver = userRepository.getUserByEmail(approverEmail);
                    User userToApprove = userRepository.getUserByEmail(userToApproveEmail);

                    if (canApprove(approver.getUserType(), userToApprove.getUserType())) {
                        userToApprove.setApproved(true, approver.getEmail());
                        userRepository.updateUser(userToApprove);
                        sendMessage("APPROVED");
                        // Notify the approved user through broadcast
                        server.sendBrodcastMessage("USER_APPROVED " + userToApproveEmail);
                    } else {
                        sendMessage("UNAUTHORIZED");
                    }
                    break;
                default:
                    sendMessage("INVALID_COMMAND");
                    break;
            }

            in.close();
        } catch (IOException | CannotWritetoFileException e) {
            throw new RuntimeException(e);
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

    private boolean canApprove(UserTypes approverType, UserTypes userType) {
        return switch (userType) {
            case High -> approverType == UserTypes.High;
            case Medium -> approverType == UserTypes.High || approverType == UserTypes.Medium;
            case Low -> true; // LOW users are auto-approved
        };
    }

    private boolean canApproveUsers(UserTypes userType) {
        return userType == UserTypes.Medium || userType == UserTypes.High;
    }
}
