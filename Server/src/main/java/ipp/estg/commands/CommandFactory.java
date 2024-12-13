package ipp.estg.commands;

import ipp.estg.Server;
import ipp.estg.commands.activatingEmergencyCommunication.RequestActivatingEmergencyCommunicationCommand;
import ipp.estg.commands.activatingEmergencyCommunication.ApproveActivatingEmergencyCommunicationRequestCommand;
import ipp.estg.commands.activatingEmergencyCommunication.GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand;
import ipp.estg.commands.auth.LoginICommand;
import ipp.estg.commands.auth.RegisterCommand;
import ipp.estg.commands.channels.ChannelCreationCommand;
import ipp.estg.commands.channels.ChannelParticipationCommand;
import ipp.estg.commands.channels.GetChannelsCommand;
import ipp.estg.commands.emergencyResourceDistribution.RequestEmergencyResourceDistributionCommand;
import ipp.estg.commands.massEvacuation.ApproveMassEvacuationRequestCommand;
import ipp.estg.commands.massEvacuation.GetMassEvacuationPendingApprovalsCommand;
import ipp.estg.commands.massEvacuation.RequestMassEvacuationCommand;
import ipp.estg.commands.messages.GetMessageCommand;
import ipp.estg.commands.messages.SendMessageCommand;
import ipp.estg.commands.notifications.GetNotificationsCommand;
import ipp.estg.commands.users.ApproveUserCommand;
import ipp.estg.commands.users.GetPendingApprovalsCommand;
import ipp.estg.commands.emergencyResourceDistribution.*;
import ipp.estg.commands.users.GetUsersCommand;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.database.repositories.*;
import ipp.estg.database.repositories.interfaces.INotificationRepository;
import ipp.estg.database.repositories.interfaces.IUserRepository;
import ipp.estg.threads.WorkerThread;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * CommandFactory is responsible for creating specific command instances based on the given command name.
 * It acts as a central place for mapping command names to corresponding command objects.
 * This approach eliminates the need for switch or if-else statements by utilizing a map of command names to command functions.
 * <p>
 * The factory pattern allows for easy extensibility, enabling new commands to be added without modifying existing code.
 * </p>
 * <p>
 * Encontrei esta classe aqui: <a href="https://stackoverflow.com/questions/126409/eliminating-switch-statements">...</a>
 * </p>
 */
public class CommandFactory {
    private final Map<String, Function<String[], ICommand>> commandMap = new HashMap<>();

    /**
     * Constructor for CommandFactory that initializes the mapping between command names and command instances.
     * It associates each command name with a function that creates the appropriate command object.
     *
     * @param workerThread                                the worker thread handling the user's requests.
     * @param userRepository                              the repository for interacting with user data.
     * @param notificationRepository                      the repository for handling notifications.
     * @param massEvacuationRepository                    the repository for mass evacuation requests.
     * @param channelRepository                           the repository for managing channels.
     * @param userMessageRepository                       the repository for handling user messages.
     * @param channelMessageRepository                    the repository for managing channel messages.
     * @param emergencyResourceDistributionRepository     the repository for emergency resource distribution requests.
     * @param activatingEmergencyCommunicationsRepository the repository for managing emergency communication activations.
     * @param server                                      the server handling client requests.
     */
    public CommandFactory(WorkerThread workerThread, IUserRepository userRepository, INotificationRepository notificationRepository, MassEvacuationRepository massEvacuationRepository, ChannelRepository channelRepository, UserMessageRepository userMessageRepository, ChannelMessageRepository channelMessageRepository, EmergencyResourceDistributionRepository emergencyResourceDistributionRepository, ActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository, Server server) {

        commandMap.put(CommandsFromClient.LOGIN, inputArray -> new LoginICommand(workerThread, userRepository, inputArray));
        commandMap.put(CommandsFromClient.REGISTER, inputArray -> new RegisterCommand(workerThread, userRepository, inputArray));
        commandMap.put(CommandsFromClient.GET_PENDING_APPROVALS, inputArray -> new GetPendingApprovalsCommand(workerThread, userRepository));
        commandMap.put(CommandsFromClient.DENY_USER, inputArray -> new ApproveUserCommand(workerThread, userRepository, inputArray, false));
        commandMap.put(CommandsFromClient.APPROVE_USER, inputArray -> new ApproveUserCommand(workerThread, userRepository, inputArray, true));
        commandMap.put(CommandsFromClient.GET_USERS, inputArray -> new GetUsersCommand(workerThread, userRepository, inputArray));
        commandMap.put(CommandsFromClient.MASS_EVACUATION, inputArray -> new RequestMassEvacuationCommand(workerThread, userRepository, massEvacuationRepository, inputArray, server));
        commandMap.put(CommandsFromClient.GET_MASS_EVACUATION_PENDING_APPROVALS, inputArray -> new GetMassEvacuationPendingApprovalsCommand(workerThread, userRepository, massEvacuationRepository));
        commandMap.put(CommandsFromClient.APPROVE_MASS_EVACUATION, inputArray -> new ApproveMassEvacuationRequestCommand(server, workerThread, userRepository, massEvacuationRepository, notificationRepository, inputArray, true));
        commandMap.put(CommandsFromClient.DENY_MASS_EVACUATION, inputArray -> new ApproveMassEvacuationRequestCommand(server, workerThread, userRepository, massEvacuationRepository, notificationRepository, inputArray, false));
        commandMap.put(CommandsFromClient.EMERGENCY_RESOURCE_DISTRIBUTION, inputArray -> new RequestEmergencyResourceDistributionCommand(workerThread, userRepository, emergencyResourceDistributionRepository, inputArray, server));
        commandMap.put(CommandsFromClient.GET_EMERGENCY_RESOURCE_DISTRIBUTION, inputArray -> new GetApproveEmergencyResourceDistributionPendingApprovalsCommand(workerThread, userRepository, emergencyResourceDistributionRepository));
        commandMap.put(CommandsFromClient.APPROVE_EMERGENCY_RESOURCE_DISTRIBUTION, inputArray -> new ApproveEmergencyResourceDistributionCommand(server, workerThread, userRepository, emergencyResourceDistributionRepository, notificationRepository, inputArray, true));
        commandMap.put(CommandsFromClient.DENY_EMERGENCY_RESOURCE_DISTRIBUTION, inputArray -> new ApproveEmergencyResourceDistributionCommand(server, workerThread, userRepository, emergencyResourceDistributionRepository, notificationRepository, inputArray, false));
        commandMap.put(CommandsFromClient.ACTIVATING_EMERGENCY_COMMUNICATIONS, inputArray -> new RequestActivatingEmergencyCommunicationCommand(workerThread, userRepository, activatingEmergencyCommunicationsRepository, inputArray, server));
        commandMap.put(CommandsFromClient.GET_ACTIVATING_EMERGENCY_COMMUNICATIONS, inputArray -> new GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand(workerThread, userRepository, activatingEmergencyCommunicationsRepository));
        commandMap.put(CommandsFromClient.APPROVE_ACTIVATING_EMERGENCY_COMMUNICATIONS, inputArray -> new ApproveActivatingEmergencyCommunicationRequestCommand(server, workerThread, userRepository, activatingEmergencyCommunicationsRepository, notificationRepository, inputArray, true));
        commandMap.put(CommandsFromClient.DENY_ACTIVATING_EMERGENCY_COMMUNICATIONS, inputArray -> new ApproveActivatingEmergencyCommunicationRequestCommand(server, workerThread, userRepository, activatingEmergencyCommunicationsRepository, notificationRepository, inputArray, false));
        commandMap.put(CommandsFromClient.GET_CHANNELS, inputArray -> new GetChannelsCommand(workerThread, channelRepository, inputArray));
        commandMap.put(CommandsFromClient.CREATE_CHANNEL, inputArray -> new ChannelCreationCommand(workerThread, userRepository, channelRepository, channelMessageRepository, inputArray, false));
        commandMap.put(CommandsFromClient.DELETE_CHANNEL, inputArray -> new ChannelCreationCommand(workerThread, userRepository, channelRepository, channelMessageRepository, inputArray, true));
        commandMap.put(CommandsFromClient.JOIN_CHANNEL, inputArray -> new ChannelParticipationCommand(workerThread, userRepository, channelRepository, inputArray, false));
        commandMap.put(CommandsFromClient.LEAVE_CHANNEL, inputArray -> new ChannelParticipationCommand(workerThread, userRepository, channelRepository, inputArray, true));
        commandMap.put(CommandsFromClient.GET_CHANNEL_MESSAGES, inputArray -> new GetMessageCommand(workerThread, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, true));
        commandMap.put(CommandsFromClient.SEND_CHANNEL_MESSAGE, inputArray -> new SendMessageCommand(server, workerThread, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, true));
        commandMap.put(CommandsFromClient.SEND_MESSAGE_TO_USER, inputArray -> new SendMessageCommand(server, workerThread, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, false));
        commandMap.put(CommandsFromClient.GET_MESSAGES_FROM_USER, inputArray -> new GetMessageCommand(workerThread, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, false));
        commandMap.put(CommandsFromClient.GET_NOTIFICATIONS, inputArray -> new GetNotificationsCommand(workerThread, notificationRepository));
    }

    /**
     * Retrieves the command associated with the provided command name and input parameters.
     * If the command name is valid, the corresponding command is returned; otherwise, null is returned.
     *
     * @param commandName the name of the command to retrieve.
     * @param inputArray  the input parameters to pass to the command's constructor.
     * @return the ICommand object associated with the command name, or null if the command is not found.
     */
    public ICommand getCommand(String commandName, String[] inputArray) {
        Function<String[], ICommand> commandFunction = commandMap.get(commandName);
        return commandFunction != null ? commandFunction.apply(inputArray) : null;
    }
}
