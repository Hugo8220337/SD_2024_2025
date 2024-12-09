package ipp.estg.commands.notifications;

import ipp.estg.commands.ICommand;
import ipp.estg.database.models.Notification;
import ipp.estg.database.repositories.interfaces.*;
import ipp.estg.threads.WorkerThread;
import ipp.estg.utils.AppLogger;
import ipp.estg.utils.JsonConverter;

import java.util.List;

public class GetNotificationsCommand implements ICommand {
    private static final AppLogger LOGGER = AppLogger.getLogger(GetNotificationsCommand.class);

    private final WorkerThread workerThread;
    private final INotificationRepository notificationRepository;

    public GetNotificationsCommand(WorkerThread workerThread, INotificationRepository notificationRepository) {
        this.workerThread = workerThread;
        this.notificationRepository = notificationRepository;
    }

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
