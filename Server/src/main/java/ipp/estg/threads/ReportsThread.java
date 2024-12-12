package ipp.estg.threads;

import ipp.estg.Server;
import ipp.estg.services.StatisticsService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A thread responsible for periodically generating and sending statistical reports about the server.
 * The reports include the total number of clients, channels, messages, and channel messages.
 * The report is broadcasted every 5 minutes to all connected clients.
 */
public class ReportsThread implements Runnable {

    /**
     * The interval (in milliseconds) at which the statistics will be updated and sent to all clients.
     * This is set to 5 minutes (300,000 milliseconds).
     */
    private final int UPDATE_INTERVAL = 300000;

    /**
     * The server instance that will broadcast the statistical reports to all connected clients.
     */
    private final Server server;

    /**
     * The service that retrieves statistical information about the server, such as the number of users, channels, and messages.
     */
    private final StatisticsService statisticsService;

    /**
     * Constructs a ReportsThread object.
     * Initializes the server and statistics service dependencies.
     *
     * @param server The server that will broadcast the statistical reports.
     */
    public ReportsThread(Server server) {
        this.server = server;
        this.statisticsService = new StatisticsService();
    }

    /**
     * Executes the thread that schedules periodic reports about the server's statistics.
     * The statistics include total clients, total channels, total messages, and total channel messages.
     * The report is sent every 5 minutes if the server is running.
     */
    @Override
    public void run() {
        Timer timer = new Timer();

        // Schedule a task to update statistics periodically
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (server.isRunning()) {
                    int totalClients = statisticsService.getNumberOfUsers();
                    int totalChannels = statisticsService.getNumberOfChannels();
                    int totalMessages = statisticsService.getNumberOfMessages();
                    int totalChannelMessages = statisticsService.getNumberOfChannelMessages();

                    server.sendBrodcastMessage("Statistics: " +
                            "\nTotal Clients: " + totalClients +
                            " \nTotal Channels: " + totalChannels +
                            " \nTotal Messages: " + totalMessages +
                            " \nTotal Channel Messages: " + totalChannelMessages);

                }
            }
        }, 0, UPDATE_INTERVAL); // Start immediately and repeat every 'updateInterval'
    }
}
