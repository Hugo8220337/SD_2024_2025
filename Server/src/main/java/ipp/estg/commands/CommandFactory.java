package ipp.estg.commands;

import ipp.estg.Server;
import ipp.estg.commands.activatingEmergencyCommunication.ActivatingEmergencyCommunicationCommand;
import ipp.estg.commands.activatingEmergencyCommunication.ApproveActivatingEmergencyCommunicationRequestCommand;
import ipp.estg.commands.activatingEmergencyCommunication.GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand;
import ipp.estg.commands.auth.LoginICommand;
import ipp.estg.commands.auth.RegisterCommand;
import ipp.estg.commands.channels.ChannelCreationCommand;
import ipp.estg.commands.channels.ChannelParticipationCommand;
import ipp.estg.commands.channels.GetChannelsCommand;
import ipp.estg.commands.emergencyResourceDistribution.AproveEmergencyResourceDistributionCommand;
import ipp.estg.commands.emergencyResourceDistribution.EmergencyResourceDistributionCommand;
import ipp.estg.commands.emergencyResourceDistribution.GetAproveEmergencyResourceDistributionPendingApprovalsCommand;
import ipp.estg.commands.massEvacuation.ApproveMassEvacuationRequestCommand;
import ipp.estg.commands.massEvacuation.GetMassEvacuationPendingApprovalsCommand;
import ipp.estg.commands.massEvacuation.MassEvacuationCommand;
import ipp.estg.commands.messages.GetMessageCommand;
import ipp.estg.commands.messages.SendMessageCommand;
import ipp.estg.commands.notifications.GetNotificationsCommand;
import ipp.estg.commands.users.ApproveUserCommand;
import ipp.estg.commands.users.GetPendingApprovalsCommand;
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
 * Factory to create commands
 * <p>
 * Encontrei esta classe aqui: <a href="https://stackoverflow.com/questions/126409/eliminating-switch-statements">...</a>
 * </p>
 */
public class CommandFactory {
    private final Map<String, Function<String[], ICommand>> commandMap = new HashMap<>();

    public CommandFactory(WorkerThread workerThread, IUserRepository userRepository, INotificationRepository notificationRepository,
                          MassEvacuationRepository massEvacuationRepository, ChannelRepository channelRepository,
                          UserMessageRepository userMessageRepository, ChannelMessageRepository channelMessageRepository,
                          EmergencyResourceDistributionRepository emergencyResourceDistributionRepository,
                          ActivatingEmergencyCommunicationsRepository activatingEmergencyCommunicationsRepository, Server server) {

        commandMap.put(CommandsFromClient.LOGIN, inputArray -> new LoginICommand(workerThread, userRepository, inputArray));
        commandMap.put(CommandsFromClient.REGISTER, inputArray -> new RegisterCommand(workerThread, userRepository, inputArray));
        commandMap.put(CommandsFromClient.GET_PENDING_APPROVALS, inputArray -> new GetPendingApprovalsCommand(workerThread, userRepository));
        commandMap.put(CommandsFromClient.DENY_USER, inputArray -> new ApproveUserCommand(workerThread, userRepository, inputArray, false));
        commandMap.put(CommandsFromClient.APPROVE_USER, inputArray -> new ApproveUserCommand(workerThread, userRepository, inputArray, true));
        commandMap.put(CommandsFromClient.GET_USERS, inputArray -> new GetUsersCommand(workerThread, userRepository, inputArray));
        commandMap.put(CommandsFromClient.MASS_EVACUATION, inputArray -> new MassEvacuationCommand(workerThread, userRepository, massEvacuationRepository, inputArray, server));
        commandMap.put(CommandsFromClient.GET_MASS_EVACUATION_PENDING_APPROVALS, inputArray -> new GetMassEvacuationPendingApprovalsCommand(workerThread, userRepository, massEvacuationRepository));
        commandMap.put(CommandsFromClient.APPROVE_MASS_EVACUATION, inputArray -> new ApproveMassEvacuationRequestCommand(server, workerThread, userRepository, massEvacuationRepository, inputArray, true));
        commandMap.put(CommandsFromClient.DENY_MASS_EVACUATION, inputArray -> new ApproveMassEvacuationRequestCommand(server, workerThread, userRepository, massEvacuationRepository, inputArray, false));
        commandMap.put(CommandsFromClient.EMERGENCY_RESOURCE_DISTRIBUTION, inputArray -> new EmergencyResourceDistributionCommand(workerThread, userRepository, emergencyResourceDistributionRepository, inputArray, server));
        commandMap.put(CommandsFromClient.GET_EMERGENCY_RESOURCE_DISTRIBUTION, inputArray -> new GetAproveEmergencyResourceDistributionPendingApprovalsCommand(workerThread, userRepository, emergencyResourceDistributionRepository));
        commandMap.put(CommandsFromClient.APPROVE_EMERGENCY_RESOURCE_DISTRIBUTION, inputArray -> new AproveEmergencyResourceDistributionCommand(server, workerThread, userRepository, emergencyResourceDistributionRepository, inputArray, true));
        commandMap.put(CommandsFromClient.DENY_EMERGENCY_RESOURCE_DISTRIBUTION, inputArray -> new AproveEmergencyResourceDistributionCommand(server, workerThread, userRepository, emergencyResourceDistributionRepository, inputArray, false));
        commandMap.put(CommandsFromClient.ACTIVATING_EMERGENCY_COMMUNICATIONS, inputArray -> new ActivatingEmergencyCommunicationCommand(workerThread, userRepository, activatingEmergencyCommunicationsRepository, inputArray, server));
        commandMap.put(CommandsFromClient.GET_ACTIVATING_EMERGENCY_COMMUNICATIONS, inputArray -> new GetApproveActivatingEmergencyCommunicationPendingApprovalsCommand(workerThread, userRepository, activatingEmergencyCommunicationsRepository));
        commandMap.put(CommandsFromClient.APPROVE_ACTIVATING_EMERGENCY_COMMUNICATIONS, inputArray -> new ApproveActivatingEmergencyCommunicationRequestCommand(server, workerThread, userRepository, activatingEmergencyCommunicationsRepository, inputArray, true));
        commandMap.put(CommandsFromClient.DENY_ACTIVATING_EMERGENCY_COMMUNICATIONS, inputArray -> new ApproveActivatingEmergencyCommunicationRequestCommand(server, workerThread, userRepository, activatingEmergencyCommunicationsRepository, inputArray, false));
        commandMap.put(CommandsFromClient.GET_CHANNELS, inputArray -> new GetChannelsCommand(workerThread, channelRepository, inputArray));
        commandMap.put(CommandsFromClient.CREATE_CHANNEL, inputArray -> new ChannelCreationCommand(workerThread, userRepository, channelRepository, false));
        commandMap.put(CommandsFromClient.DELETE_CHANNEL, inputArray -> new ChannelCreationCommand(workerThread, userRepository, channelRepository, true));
        commandMap.put(CommandsFromClient.JOIN_CHANNEL, inputArray -> new ChannelParticipationCommand(workerThread, userRepository, channelRepository, inputArray, false));
        commandMap.put(CommandsFromClient.LEAVE_CHANNEL, inputArray -> new ChannelParticipationCommand(workerThread, userRepository, channelRepository, inputArray, true));
        commandMap.put(CommandsFromClient.GET_CHANNEL_MESSAGES, inputArray -> new GetMessageCommand(workerThread, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, true));
        commandMap.put(CommandsFromClient.SEND_CHANNEL_MESSAGE, inputArray -> new SendMessageCommand(server, workerThread, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, true));
        commandMap.put(CommandsFromClient.SEND_MESSAGE_TO_USER, inputArray -> new SendMessageCommand(server, workerThread, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, false));
        commandMap.put(CommandsFromClient.GET_MESSAGES_FROM_USER, inputArray -> new GetMessageCommand(workerThread, userRepository, channelRepository, channelMessageRepository, userMessageRepository, inputArray, false));
        commandMap.put(CommandsFromClient.GET_NOTIFICATIONS, inputArray -> new GetNotificationsCommand(workerThread, notificationRepository, inputArray));
    }

    public ICommand getCommand(String commandName, String[] inputArray) {
        Function<String[], ICommand> commandFunction = commandMap.get(commandName);
        return commandFunction != null ? commandFunction.apply(inputArray) : null;
    }
}
