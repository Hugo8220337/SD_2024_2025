package ipp.estg.commands.notifications;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Notification;
import ipp.estg.database.repositories.interfaces.*;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.List;

/**
 * Command class to handle fetching notifications for a user.
 * Implements the {@link ICommand} interface.
 */
public class GetNotificationsCommand implements ICommand {

    /**
     * Logger for logging events and errors.
     */
    private static final AppLogger LOGGER = AppLogger.getLogger(GetNotificationsCommand.class);

    /**
     * Worker thread responsible for handling the user request.
     */
    private final WorkerThread workerThread;

    /**
     * Repository to access notification data.
     */
    private final INotificationRepository notificationRepository;

    /**
     * Constructs a GetNotificationsCommand instance.
     *
     * @param workerThread         the worker thread handling the user's request.
     * @param notificationRepository the repository for fetching user notifications.
     */
    public GetNotificationsCommand(WorkerThread workerThread, INotificationRepository notificationRepository) {
        this.workerThread = workerThread;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Executes the command to retrieve and send notifications for the current user.
     * Fetches the notifications from the repository and sends them to the user.
     * If the user is not logged in, an error message is sent.
     */
    @Override
    public void execute() {
        int userId = workerThread.getCurrentUserId();
        if (userId == -1) {
            workerThread.sendMessage("ERROR: User not logged in");
            LOGGER.error("User not logged in");
            return;
        }

        try {
            List<Notification> notifications = notificationRepository.getAllByUserId(userId);

            JsonConverter jsonConverter = new JsonConverter();
            String json = jsonConverter.toJson(notifications);

            workerThread.sendMessage(json);

            LOGGER.info("Sent notifications to user with id " + userId);
        } catch (Exception e) {
            workerThread.sendMessage("ERROR: Could not get notifications");
            LOGGER.error("Could not get notifications: " + e.getMessage());
        }
    }
}
