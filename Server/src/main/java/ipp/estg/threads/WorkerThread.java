package ipp.estg.threads;

import ipp.estg.Server;
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
        this.notificationRepository = new NotificationRepository(DatabaseFiles.NOTIFICATIONS_FILE, userRepository);
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
                case CommandsFromClient.GET_USERS:
                    command = new GetUsersCommand(this, userRepository, inputArray);
                    break;
                case CommandsFromClient.MASS_EVACUATION:
                    command = new MassEvacuationCommand(this, userRepository, massEvacuationRepository, inputArray, server);
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
                case CommandsFromClient.EMERGENCY_RESOURCE_DISTRIBUTION:
                    command = new EmergencyResourceDistributionCommand(this, userRepository, emergencyResourceDistributionRepository, inputArray, server);
                    break;
                case CommandsFromClient.GET_EMERGENCY_RESOURCE_DISTRIBUTION:
                    command = new GetAproveEmergencyResourceDistributionPendingApprovalsCommand(this, userRepository, emergencyResourceDistributionRepository, inputArray);
                    break;
                case CommandsFromClient.APPROVE_EMERGENCY_RESOURCE_DISTRIBUTION:
                    command = new AproveEmergencyResourceDistributionCommand(server, this, userRepository, emergencyResourceDistributionRepository, inputArray, true);
                    break;
                case CommandsFromClient.DENY_EMERGENCY_RESOURCE_DISTRIBUTION:
                    command = new AproveEmergencyResourceDistributionCommand(server, this, userRepository, emergencyResourceDistributionRepository, inputArray, false);
                    break;
                case CommandsFromClient.ACTIVATING_EMERGENCY_COMMUNICATIONS:
                    command = new ActivatingEmergencyCommunicationCommand(this, userRepository, activatingEmergencyCommunicationsRepository, inputArray, server);
                    break;
                case CommandsFromClient.GET_ACTIVATING_EMERGENCY_COMMUNICATIONS:
                    command = new GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand(this, userRepository, activatingEmergencyCommunicationsRepository, inputArray);
                    break;
                case CommandsFromClient.APPROVE_ACTIVATING_EMERGENCY_COMMUNICATIONS:
                    command = new ApproveActivatingEmergencyCommunicationRequestCommand(server, this, userRepository, activatingEmergencyCommunicationsRepository, inputArray, true);
                    break;
                case CommandsFromClient.DENY_ACTIVATING_EMERGENCY_COMMUNICATIONS:
                    command = new ApproveActivatingEmergencyCommunicationRequestCommand(server, this, userRepository, activatingEmergencyCommunicationsRepository, inputArray, false);
                    break;
                case CommandsFromClient.GET_CHANNELS:
                    command = new GetChannelsCommand(this, channelRepository, inputArray);
                    break;
                case CommandsFromClient.CREATE_CHANNEL:
                    command = new ChannelCreationCommand(this, userRepository, channelRepository, inputArray, false);
                    break;
                case CommandsFromClient.DELETE_CHANNEL:
                    command = new ChannelCreationCommand(this, userRepository, channelRepository, inputArray, true);
                    break;
                case CommandsFromClient.JOIN_CHANNEL:
                    command = new ChannelParticipationCommand(this, userRepository, channelRepository, inputArray, false);
                    break;
                case CommandsFromClient.LEAVE_CHANNEL:
                    command = new ChannelParticipationCommand(this, userRepository, channelRepository, inputArray, true);
                    break;
                case CommandsFromClient.GET_CHANNEL_MESSAGES:
                    command = new GetMessageCommand(this, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, true);
                    break;
                case CommandsFromClient.SEND_CHANNEL_MESSAGE:
                    command = new SendMessageCommand(this, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, true);
                    break;
                case CommandsFromClient.SEND_MESSAGE_TO_USER:
                    command = new SendMessageCommand(this, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, false);
                    break;
                case CommandsFromClient.GET_MESSAGES_FROM_USER:
                    command = new GetMessageCommand(this, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, false);
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
        } catch (Exception e) {
            LOGGER.error("Error while running the server: " + e.getMessage());
        } finally {
            try {
                server.removeClientFromList(this);
                clientSocket.close();
                in.close();
                out.close();

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
