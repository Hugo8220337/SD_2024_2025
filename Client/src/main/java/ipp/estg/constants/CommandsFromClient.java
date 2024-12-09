package ipp.estg.constants;

public class CommandsFromClient {
    /**
     * Authentication
     */
    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";

    /**
     * User
     */
    public static final String APPROVE_USER = "APPROVE_USER";
    public static final String DENY_USER = "DENY_USER";
    public static final String GET_PENDING_APPROVALS = "PENDING_APPROVALS";
    public static final String GET_USERS = "GET_USERS";

    /**
     * Mass Evacuation
     */
    public static final String MASS_EVACUATION = "MASS_EVACUATION";
    public static final String GET_MASS_EVACUATION_PENDING_APPROVALS = "GET_MASS_EVACUATION_PENDING_APPROVALS";
    public static final String APPROVE_MASS_EVACUATION = "APPROVE_MASS_EVACUATION";
    public static final String DENY_MASS_EVACUATION = "DENY_MASS_EVACUATION";

    /**
     * Emergency Resource Distribution
     */
    public static final String EMERGENCY_RESOURCE_DISTRIBUTION = "EMERGENCY_RESOURCE_DISTRIBUTION";
    public static final String GET_EMERGENCY_RESOURCE_DISTRIBUTION = "GET_EMERGENCY_RESOURCE_DISTRIBUTION";
    public static final String APPROVE_EMERGENCY_RESOURCE_DISTRIBUTION = "APPROVE_EMERGENCY_RESOURCE_DISTRIBUTION";
    public static final String DENY_EMERGENCY_RESOURCE_DISTRIBUTION = "DENY_EMERGENCY_RESOURCE_DISTRIBUTION";

    /**
     * Activating Emergency Communications
     */
    public static final String ACTIVATING_EMERGENCY_COMMUNICATIONS = "ACTIVATING_EMERGENCY_COMMUNICATIONS";
    public static final String GET_ACTIVATING_EMERGENCY_COMMUNICATIONS = "GET_ACTIVATING_EMERGENCY_COMMUNICATIONS";
    public static final String APPROVE_ACTIVATING_EMERGENCY_COMMUNICATIONS = "APPROVE_ACTIVATING_EMERGENCY_COMMUNICATIONS";
    public static final String DENY_ACTIVATING_EMERGENCY_COMMUNICATIONS = "DENY_ACTIVATING_EMERGENCY_COMMUNICATIONS";

    /**
     * Channels
     */
    public static final String GET_CHANNELS = "GET_CHANNELS";
    public static final String CREATE_CHANNEL = "CREATE_CHANNEL";
    public static final String DELETE_CHANNEL = "DELETE_CHANNEL";
    public static final String JOIN_CHANNEL = "JOIN_CHANNEL";
    public static final String LEAVE_CHANNEL = "LEAVE_CHANNEL";

    /**
     * Chat
     */
    public static final String GET_MESSAGES_FROM_USER = "GET_MESSAGES_FROM_USER";
    public static final String SEND_MESSAGE_TO_USER = "SEND_MESSAGE_TO_USER";
    public static final String GET_CHANNEL_MESSAGES = "GET_CHANNEL_MESSAGES";
    public static final String SEND_CHANNEL_MESSAGE = "SEND_CHANNEL_MESSAGE";

    /**
     * Notifications
     */
    public static final String GET_NOTIFICATIONS = "GET_NOTIFICATIONS";
}