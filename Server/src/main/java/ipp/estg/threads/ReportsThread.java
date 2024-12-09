package ipp.estg.threads;

import ipp.estg.Server;
import ipp.estg.services.StatisticsService;

import java.util.Timer;
import java.util.TimerTask;

public class ReportsThread implements Runnable {
    private final int UPDATE_INTERVAL = 300000; // 5 minutes
    private final Server server;
    private final StatisticsService statisticsService;

    public ReportsThread(Server server) {
        this.server = server;
        this.statisticsService = new StatisticsService();
    }

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
