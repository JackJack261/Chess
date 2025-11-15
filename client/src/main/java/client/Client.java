package client;

import java.util.Scanner;

public class Client {

    private final ServerFacade serverFacade;
    private boolean isLoggedIn = false;
    private String authToken = null;

    public Client(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
    }

    // You'll add more methods here

    // Inside your Client.java class

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("♕ Welcome to Chess. Type 'help' to get started.");

        while (true) {
            // 1. PRINT PROMPT (based on state)
            if (isLoggedIn) {
                System.out.print("[LOGGED_IN] >>> ");
            } else {
                System.out.print("[LOGGED_OUT] >>> ");
            }

            // 2. READ INPUT
            String line = scanner.nextLine();
            String[] args = line.split(" ");
            String command = args.length > 0 ? args[0].toLowerCase() : "help";

            // 3. EVALUATE (call helper method)
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

    // 4. Create stubs for your command handlers
    private void handlePreloginCommands(String command, String[] args) {

        // Debug
        System.out.println("Pre-login command: " + command);

        // Help
        if (command.equals("help")) {
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
            System.out.println("login <USERNAME> <PASSWORD> - to play chess");
            System.out.println("quit - playing chess");
            System.out.println("help - with possible commands");
        }

        // Quit
        else if (command.equals("quit")) {
            System.out.println("Goodbye!");
            System.exit(0);
        }

        // Login
        else if (command.equals("login")) {

        }

        // Register
        else {

        }


    }

    private void handlePostloginCommands(String command, String[] args) {
        // TODO: Implement this later
        System.out.println("Post-login command: " + command);
    }
}