package ipp.estg.threads;

import ipp.estg.Server;
import ipp.estg.commands.CommandFactory;
import ipp.estg.commands.messages.GetMessageCommand;
import ipp.estg.commands.ICommand;
import ipp.estg.commands.messages.SendMessageCommand;
import ipp.estg.commands.emergencyResourceDistribution.AproveEmergencyResourceDistributionCommand;
import ipp.estg.commands.emergencyResourceDistribution.EmergencyResourceDistributionCommand;
import ipp.estg.commands.emergencyResourceDistribution.GetAproveEmergencyResourceDistributionPendingApprovalsCommand;
import ipp.estg.commands.activatingEmergencyCommunication.ActivatingEmergencyCommunicationCommand;
import ipp.estg.commands.activatingEmergencyCommunication.ApproveActivatingEmergencyCommunicationRequestCommand;
import ipp.estg.commands.activatingEmergencyCommunication.GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand;
import ipp.estg.commands.auth.LoginICommand;
import ipp.estg.commands.auth.RegisterCommand;
import ipp.estg.commands.channels.ChannelCreationCommand;
import ipp.estg.commands.channels.ChannelParticipationCommand;
import ipp.estg.commands.channels.GetChannelsCommand;
import ipp.estg.commands.massEvacuation.ApproveMassEvacuationRequestCommand;
import ipp.estg.commands.massEvacuation.GetMassEvacuationPendingApprovalsCommand;
import ipp.estg.commands.massEvacuation.MassEvacuationCommand;
import ipp.estg.commands.notifications.GetNotificationsCommand;
import ipp.estg.commands.users.ApproveUserCommand;
import ipp.estg.commands.users.GetPendingApprovalsCommand;
import ipp.estg.commands.users.GetUsersCommand;
import ipp.estg.constants.CommandsFromServer;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.constants.DatabaseFiles;
import ipp.estg.database.repositories.*;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.utils.AppLogger;
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

    private static final AppLogger LOGGER = AppLogger.getLogger(WorkerThread.class);

    /**
     * Database Repositories
     */
    private final IUserRepository userRepository;
    private final INotificationRepository notificationRepository;
    private final MassEvacuationRepository massEvacuationRepository;
    private final ChannelRepository channelRepository;
    private final UserMessageRepository userMessageRepository;
    private final ChannelMessageRepository channelMessageRepository;
    private final EmergencyResourceDistributionRepository emergencyResourceDistributionRepository;
    private final ActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository;


    private final Server server;
    private final Socket clientSocket;

    private BufferedReader in = null;
    private PrintWriter out = null;

    public WorkerThread(Server server, Socket clientSocket) {
        super("server.WorkerThread");
        this.server = server;
        this.clientSocket = clientSocket;

        this.userRepository = new UserRepository(DatabaseFiles.USERS_FILE);
        this.notificationRepository = new NotificationRepository(DatabaseFiles.NOTIFICATIONS_FILE);
        this.massEvacuationRepository = new MassEvacuationRepository(DatabaseFiles.MASS_EVACUATIONS_FILE);
        this.emergencyResourceDistributionRepository = new EmergencyResourceDistributionRepository(DatabaseFiles.EMERGENCY_RESOURCE_DISTRIBUTION_FILE);
        this.activatingEmergencyCommunicationsRepository = new ActivatingEmergencyCommunicationsRepository(DatabaseFiles.ACTIVATING_EMERGENCY_COMMUNICATIONS_FILE);
        this.channelRepository = new ChannelRepository(DatabaseFiles.CHANNELS_FILE);
        this.userMessageRepository = new UserMessageRepository(DatabaseFiles.USER_MESSAGES_FILE);
        this.channelMessageRepository = new ChannelMessageRepository(DatabaseFiles.CHANNEL_MESSAGES_FILE);
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

            CommandFactory commandFactory = new CommandFactory(this, userRepository, notificationRepository, massEvacuationRepository, channelRepository,
                    userMessageRepository, channelMessageRepository, emergencyResourceDistributionRepository, activatingEmergencyCommunicationsRepository, server);

            String input;
            String[] inputArray;

            input = in.readLine();
            inputArray = StringUtils.splitCommandLine(input);

            // Get command
            ICommand command = commandFactory.getCommand(inputArray[0], inputArray);

            // Execute command
            if (command != null) {
                command.execute();
            } else {
                sendMessage(CommandsFromServer.INVALID_COMMAND);
            }

            in.close();
        } catch (Exception e) {
            LOGGER.error("Error while running the server: " + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                server.removeClientFromList(this);

                LOGGER.info("Client disconnected");
            } catch (IOException e) {
                LOGGER.error("Error while closing the server: " + e.getMessage());
            }
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

}
