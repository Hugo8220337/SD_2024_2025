package ipp.estg;

import ipp.estg.pages.auth.login.LoginPage;

public class Main {
    private static Client client;

    public static void main(String[] args) {
        try {
            client = new Client();
        } catch (Exception e) {
            System.out.println("Error on client boot: " + e.getMessage());
            return;
        }

        LoginPage login = new LoginPage(client);
        login.setVisible(true);
    }
}