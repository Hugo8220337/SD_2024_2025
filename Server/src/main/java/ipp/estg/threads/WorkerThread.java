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
 * A thread responsible for handling a specific client connection.
 * This thread listens for incoming commands from the client, processes them, and sends appropriate responses.
 * It also manages the connection to the client and interacts with the necessary repositories for data retrieval and updates.
 */
public class WorkerThread extends Thread {

    /**
     * Logger for the WorkerThread class (log4j)
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(WorkerThread.class);

    /**
     * Database Repositories used for various operations related to users, notifications, messages, etc.
     */
    private final IUserRepository userRepository;
    private final INotificationRepository notificationRepository;
    private final MassEvacuationRepository massEvacuationRepository;
    private final ChannelRepository channelRepository;
    private final UserMessageRepository userMessageRepository;
    private final ChannelMessageRepository channelMessageRepository;
    private final EmergencyResourceDistributionRepository emergencyResourceDistributionRepository;
    private final ActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository;

    /**
     * The server instance that the worker thread communicates with.
     */
    private final Server server;

    /**
     * The socket associated with the client connection.
     */
    private final Socket clientSocket;

    /**
     * The current user ID of the client.
     */
    private int currentUserId = -1;

    /**
     * Input and output streams for communication with the client.
     */
    private BufferedReader in = null;
    private PrintWriter out = null;

    /**
     * Constructs a new WorkerThread instance.
     * Initializes the socket connection, the necessary repositories, and sets up the input and output streams for communication.
     *
     * @param server The server instance that the worker thread communicates with.
     * @param clientSocket The socket representing the client's connection.
     */
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
     * Sends a message to the connected client.
     *
     * @param message The message to be sent to the client.
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * The main method of the WorkerThread, responsible for processing client commands.
     * It reads input from the client, parses it, and executes the corresponding command.
     * If the command is invalid, it sends an error message to the client.
     */
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

    /**
     * Gets the current user ID of the connected client.
     *
     * @return The current user ID.
     */
    public int getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Sets the current user ID of the connected client.
     *
     * @param currentUserId The current user ID to set.
     */
    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    /**
     * Gets the client socket associated with the worker thread.
     *
     * @return The client socket.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }
}