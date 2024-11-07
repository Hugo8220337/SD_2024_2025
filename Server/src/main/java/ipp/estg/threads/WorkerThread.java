package ipp.estg.threads;

import ipp.estg.Server;
import ipp.estg.constants.CommandsFromClient;
import ipp.estg.constants.DatabaseFiles;
import ipp.estg.database.models.User;
import ipp.estg.database.repositories.FileNotificationRepository;
import ipp.estg.database.repositories.FileUserRepository;
import ipp.estg.database.repositories.interfaces.NotificationRepository;
import ipp.estg.database.repositories.interfaces.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread responsável por tratar de um cliente em específico
 */
public class WorkerThread extends Thread {

    /**
     * Database Repositories
     */
    private UserRepository userRepository;
    private NotificationRepository notificationRepository;

    private Server server;
    private Socket clientSocket;

    private BufferedReader in = null;
    private PrintWriter out = null;

    public WorkerThread(Server server, Socket clientSocket) {
        super("server.WorkerThread");
        this.server = server;
        this.clientSocket = clientSocket;

        this.userRepository = new FileUserRepository(DatabaseFiles.USERS_FILE);
        this.notificationRepository = new FileNotificationRepository(DatabaseFiles.NOTIFICATIONS_FILE);
        // TODO falta um para os logs
    }

    /**
     * Envia mensagem para o utilizador que está conectado
     *
     * @param message mensagem a ser enviada
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String input;
            String[] inputArray;

            input = in.readLine();
            inputArray = input.split(" ");

            switch (inputArray[0]) {
                case CommandsFromClient.LOGIN:
                    User user = userRepository.login(inputArray[1], inputArray[2]);
                    if(user == null) {
                        sendMessage("FAILIURE");
                        break;
                    }
                    sendMessage("SUCCESS");
                    break;
                case CommandsFromClient.REGISTER:
                    sendMessage("GO DO THE DISHES");
                    break;
                case "3":
                    sendMessage("3 - Opção 3");
                    break;
                default:
                    sendMessage("Opção inválida");
                    break;
            }

            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                server.removeClientFromList(this);
                clientSocket.close();
                in.close();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
