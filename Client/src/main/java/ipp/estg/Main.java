package ipp.estg;

import ipp.estg.pages.auth.login.LoginPage;
import ipp.estg.utils.AppLogger;


public class Main {
    private static final AppLogger LOGGER = AppLogger.getLogger(Main.class);
    private static Client client;

    public static void main(String[] args) {
        try {
            client = new Client();
        } catch (Exception e) {
            System.out.println("Error on client boot: " + e.getMessage());
            LOGGER.error("Error on client boot: " + e.getMessage());
            return;
        }

        LoginPage login = new LoginPage(client);
        login.setVisible(true);
    }
}