package ipp.estg.threads;

import ipp.estg.Server;
import ipp.estg.commands.CommandFactory;
import ipp.estg.commands.ICommand;
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
    private int currentUserId = -1;

    private BufferedReader in = null;
    private PrintWriter out = null;

    public WorkerThread(Server server, Socket clientSocket) {
        super("server.WorkerThread");
        this.server = server;
        this.clientSocket = clientSocket;

        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            LOGGER.error("Error while creating the WorkerThread: " + e.getMessage());
        }

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
            CommandFactory commandFactory = new CommandFactory(this, userRepository, notificationRepository, massEvacuationRepository, channelRepository,
                    userMessageRepository, channelMessageRepository, emergencyResourceDistributionRepository, activatingEmergencyCommunicationsRepository, server);

            String input;
            String[] inputArray;

            while ((input = in.readLine()) != null) {
                inputArray = StringUtils.splitCommandLine(input);

                // Get command
                ICommand command = commandFactory.getCommand(inputArray[0], inputArray);

                // Execute command
                if (command != null) {
                    command.execute();
                } else {
                    sendMessage("ERROR: Invalid Command");
                }
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

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

}