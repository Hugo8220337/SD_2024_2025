package ipp.estg.notifications;

import com.google.gson.JsonObject;
import ipp.estg.database.models.Notification;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class NotificationSender {
    private Socket socket = null;
    private final Notification notification;
    private PrintWriter out = null;

    public NotificationSender(Notification notification) {
        super();
        this.notification = notification;
    }

    /**
     * Envia uma notificação para os gestores locais
     */
    public void run() {
        try {
            socket = new Socket("127.0.0.1", 2050);
            JsonObject reportObject = new JsonObject();

            reportObject.addProperty("NotificationDate", notification.getNotificationDate());
            reportObject.addProperty("UserEmail", notification.getUserEmail());
            reportObject.addProperty("Message", notification.getMessage());

            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(Arrays.toString(new String[]{"SendNotificationToLocals", reportObject.toString()}));
        } catch (IOException e) {
            System.err.println("Error sending notification: " + e.getMessage());
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }

        if (out != null) {
            out.close();
        }
    }
}
