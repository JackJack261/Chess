package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import models.*;
import requestsandresults.GameInfo;

import java.util.List;
import java.util.Scanner;

public class Client {

    private final ServerFacade serverFacade;
    private boolean isLoggedIn = false;
    private String authToken = null;
    private List<GameInfo> displayedGames;

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

    private void handlePostloginCommands(String command, String[] args) throws DataAccessException {
        System.out.println("Post-login command: " + command);


        // Logout
        if (command.equals("logout")) {
            if (args.length == 1) {

                serverFacade.logout(authToken);

                this.isLoggedIn = false;
                this.authToken = null;
                this.displayedGames = null;

                System.out.println("You have been logged out.");
            } else {
                System.out.println("Usage: logout");
            }
        }

        // Create Game
        else if (command.equals("create")) {
            if (args.length == 2) {
                String gameName = args[1];

                int gameID = serverFacade.createGame(authToken, gameName);

                System.out.println("Game created.");
            } else {
                System.out.println("Usage: create <NAME>");
            }
        }

        // List
        else if (command.equals("list")) {
            if (args.length == 1) {

                this.displayedGames = serverFacade.listGames(authToken);

                if (displayedGames.isEmpty()) {
                    System.out.println("No games available.");
                } else {
                    System.out.println("Available games:");
                    for (int i = 0; i < displayedGames.size(); i++) {
                        GameInfo game = displayedGames.get(i);
                        System.out.printf("  %d. %s (White: %s, Black: %s)\n",
                                (i + 1),
                                game.gameName(),
                                game.whiteUsername() != null ? game.whiteUsername() : "empty",
                                game.blackUsername() != null ? game.blackUsername() : "empty");
                    }
                }
            } else {
                System.out.println("Usage: list games");
            }
        }
        // Play/Join game
        else if (command.equals("join")) {
            if (args.length == 3) {
                try {
                    int gameNumber = Integer.parseInt(args[1]);
                    String playerColor = args[2].toUpperCase(); // "WHITE" or "BLACK"

                    // Check if user has listed games first
                    if (this.displayedGames == null) {
                        System.out.println("Error: You must 'list games' first.");
                        return;
                    }

                    // Check for valid game number
                    if (gameNumber < 1 || gameNumber > this.displayedGames.size()) {
                        System.out.println("Error: Invalid game number.");
                        return;
                    }

                    // Check for valid color
                    if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                        System.out.println("Error: Invalid color. Must be WHITE or BLACK.");
                        return;
                    }

                    // Get the real gameID from the stored list
                    GameInfo game = this.displayedGames.get(gameNumber - 1);

                    // Call the facade to join
                    serverFacade.joinGame(authToken, playerColor, game.gameID());

                    System.out.println("Joined game as " + playerColor + ".");

                    // Draw the board (as required by Phase 5)
                    drawChessBoard(playerColor.equals("WHITE") ? ChessGame.Side.WHITE : ChessGame.Side.BLACK);

                } catch (NumberFormatException e) {
                    System.out.println("Error: Game ID must be a number.");
                }
            } else {
                System.out.println("Usage: play game <ID> <WHITE|BLACK>");
            }
        }

        // Observe game

        // Quit
        else if (command.equals("quit")) {
            System.out.println("Goodbye!");
            System.exit(0);
        }

        // Help
        else {
            System.out.println("create <NAME> - a game");
            System.out.println("list - games");
            System.out.println("join <ID> [WHITE|BLACK] - a game");
            System.out.println("observe <ID> - a game");
            System.out.println("logout - when you are done");
            System.out.println("quit - playing chess");
            System.out.println("help - with possible commands");
        }

    }
}