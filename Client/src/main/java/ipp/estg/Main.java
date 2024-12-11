package ipp.estg;

import ipp.estg.pages.auth.login.LoginPage;
import ipp.estg.utils.AppLogger;


public class Main {
    private static final AppLogger LOGGER = AppLogger.getLogger(Main.class);

    public static void main(String[] args) {
        Client client;

        try {
            LOGGER.info("Starting client...");

            client = new Client();
        } catch (Exception e) {
            LOGGER.error("Error on client boot, maybe server is shutdown: " + e.getMessage());
            return;
        }

        LoginPage login = new LoginPage(client);
        login.setVisible(true);
    }
}