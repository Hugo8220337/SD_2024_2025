package ipp.estg.threads;

import ipp.estg.Server;
import ipp.estg.commands.*;
import ipp.estg.constants.CommandsFromServer;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.constants.DatabaseFiles;
import ipp.estg.database.repositories.*;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread responsável por tratar de um cliente em específico
 */
public class WorkerThread extends Thread {

    /**
     * Database Repositories
     */
    private IUserRepository userRepository;
    private INotificationRepository INotificationRepository;
    private MassEvacuationRepository massEvacuationRepository;
    private ChannelRepository channelRepository;
    private MessageRepository messageRepository;


    private Server server;
    private Socket clientSocket;

    private BufferedReader in = null;
    private PrintWriter out = null;

    public WorkerThread(Server server, Socket clientSocket) {
        super("server.WorkerThread");
        this.server = server;
        this.clientSocket = clientSocket;

        this.userRepository = new UserRepository(DatabaseFiles.USERS_FILE);
        this.INotificationRepository = new NotificationRepository(DatabaseFiles.NOTIFICATIONS_FILE, userRepository);
        this.massEvacuationRepository = new MassEvacuationRepository(DatabaseFiles.MASS_EVACUATIONS_FILE);
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
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String input;
            String[] inputArray;

            input = in.readLine();
            inputArray = StringUtils.splitCommandLine(input);

            ICommand command = null;
            switch (inputArray[0]) {
                case CommandsFromClient.LOGIN:
                    command = new LoginICommand(this, userRepository, inputArray);
                    break;
                case CommandsFromClient.REGISTER:
                    command = new RegisterCommand(this, userRepository, inputArray);
                    break;
                case CommandsFromClient.GET_PENDING_APPROVALS:
                    command = new GetPendingApprovalsCommand(this, userRepository, inputArray);
                    break;
                case CommandsFromClient.DENY_USER:
                    command = new ApproveUserCommand(this, userRepository, inputArray, false);
                    break;
                case CommandsFromClient.APPROVE_USER:
                    command = new ApproveUserCommand(this, userRepository, inputArray, true);
                    break;
                case CommandsFromClient.MASS_EVACUATION:
                    command = new MassEvacuationCommand(this, userRepository, massEvacuationRepository, inputArray);
                    break;
                case CommandsFromClient.GET_MASS_EVACUATION_PENDING_APPROVALS:
                    command = new GetMassEvacuationPendingApprovalsCommand(this, userRepository, massEvacuationRepository, inputArray);
                    break;
                case CommandsFromClient.APPROVE_MASS_EVACUATION:
                    command = new ApproveMassEvacuationRequestCommand(server, this, userRepository, massEvacuationRepository, inputArray, true);
                    break;
                case CommandsFromClient.DENY_MASS_EVACUATION:
                    command = new ApproveMassEvacuationRequestCommand(server, this, userRepository, massEvacuationRepository, inputArray, false);
                    break;
                default:
                    sendMessage(CommandsFromServer.INVALID_COMMAND);
                    break;
            }

            // Execute command
            if (command != null) {
                command.execute();
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

}
