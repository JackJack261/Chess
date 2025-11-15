package client;

import dataaccess.DataAccessException;
import models.*;

import java.util.Scanner;

public class Client {

    private final ServerFacade serverFacade;
    private boolean isLoggedIn = false;

    public Client(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("♕ Welcome to Chess. Type 'help' to get started.");

        while (true) {
            if (isLoggedIn) {
                System.out.print("[LOGGED_IN] >>> ");
            } else {
                System.out.print("[LOGGED_OUT] >>> ");
            }

            String line = scanner.nextLine();
            String[] args = line.split(" ");
            String command = args.length > 0 ? args[0].toLowerCase() : "help";

            try {
                if (isLoggedIn) {
                    handlePostloginCommands(command, args);
                } else {
                    handlePreloginCommands(command, args);
                }
            } catch (Exception e) {
                // 4. PRINT ERROR (user-friendly!)
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handlePreloginCommands(String command, String[] args) throws DataAccessException {

        // Debug
        System.out.println("Pre-login command: " + command);

        // Register
        String authToken = null;
        if (command.equals("register")) {
            if (args.length == 4) {
                String username = args[1];
                String password = args[2];
                String email = args[3];


                AuthData authData = serverFacade.register(username, password, email);

                this.isLoggedIn = true;
                assert authData != null;
                authToken = authData.authToken();

                System.out.println("Welcome, " + authData.username());
            } else {
                System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            }
        }

        // Quit
        else if (command.equals("quit")) {
            System.out.println("Goodbye!");
            System.exit(0);
        }

        // Login
        else if (command.equals("login")) {
            if (args.length == 3) {
                String username = args[1];
                String password = args[2];

                AuthData authData = serverFacade.login(username, password);


                this.isLoggedIn = true;
                assert authData != null;
                authToken = authData.authToken();

                System.out.println("Welcome, " + authData.username());
            } else {
                System.out.println("Usage: login <USERNAME> <PASSWORD>");
            }
        }

        // Help
        else {
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
            System.out.println("login <USERNAME> <PASSWORD> - to play chess");
            System.out.println("quit - playing chess");
            System.out.println("help - with possible commands");
        }


    }

    private void handlePostloginCommands(String command, String[] args) {
        System.out.println("Post-login command: " + command);

        if (command.equals("quit")) {
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }
}