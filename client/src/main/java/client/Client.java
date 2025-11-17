package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import models.*;
import requestsandresults.GameInfo;
import client.ChessboardPrinter;
import ui.EscapeSequences.*;

import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {

    private final ServerFacade serverFacade;
    private boolean isLoggedIn = false;
    private String authToken = null;
    private List<GameInfo> displayedGames;
    private final ChessboardPrinter boardPrinter;

    public Client(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.boardPrinter = new ChessboardPrinter();
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
            System.out.print(SET_TEXT_COLOR_BLUE);
            System.out.print("register <USERNAME> <PASSWORD> <EMAIL>");
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.println(" - to create an account");

            System.out.print(SET_TEXT_COLOR_BLUE);
            System.out.print("login <USERNAME> <PASSWORD>");
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.println(" - to play chess");

            System.out.print(SET_TEXT_COLOR_BLUE);
            System.out.print("quit");
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.println("- playing chess");

            System.out.print(SET_TEXT_COLOR_BLUE);
            System.out.print("help");
            System.out.print(SET_TEXT_COLOR_WHITE);
            System.out.println(" - with possible commands");
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

                    if (this.displayedGames == null) {
                        System.out.println("Error: You must 'list games' first.");
                        return;
                    }

                    if (gameNumber < 1 || gameNumber > this.displayedGames.size()) {
                        System.out.println("Error: Invalid game number.");
                        return;
                    }

                    if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                        System.out.println("Error: Invalid color. Must be WHITE or BLACK.");
                        return;
                    }

                    GameInfo game = this.displayedGames.get(gameNumber - 1);

                    serverFacade.joinGame(authToken, playerColor, game.gameID());

                    System.out.println("Joined game as " + playerColor + ".");

                    // Draw the board (as required by Phase 5)
                    drawChessBoard(playerColor);

                } catch (NumberFormatException e) {
                    System.out.println("Error: Game ID must be a number.");
                }
            } else {
                System.out.println("Usage: play game <ID> <WHITE|BLACK>");
            }
        }

        // Observe game

        else if (command.equals("observe")) {
            if (args.length == 2) {
                try {
                    int gameNumber = Integer.parseInt(args[1]);

                    if (this.displayedGames == null) {
                        System.out.println("Error: You must 'list games' first.");
                        return;
                    }

                    if (gameNumber < 1 || gameNumber > this.displayedGames.size()) {
                        System.out.println("Error: Invalid game number.");
                        return;
                    }

                    GameInfo game = this.displayedGames.get(gameNumber - 1);

                    System.out.println("Observing game.");

                    drawChessBoard("WHITE");

                } catch (NumberFormatException e) {
                    System.out.println("Error: Game ID must be a number.");
                }
            } else {
                System.out.println("Usage: observe game <ID>");
            }
        }

        // Quit
        else if (command.equals("quit")) {
            System.out.println("Goodbye!");
            System.exit(0);
        }

        // Clear DB
        else if (command.equals("clear")) {
            System.out.println("[+] Clearing Database...");

            serverFacade.clear();
            this.isLoggedIn = false;
        }

        // Help
        else {
            System.out.println(SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_WHITE + " - a game");
            System.out.println(SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_WHITE + " - games");
            System.out.println(SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]" + SET_TEXT_COLOR_WHITE + " - a game");
            System.out.println(SET_TEXT_COLOR_BLUE + "observe <ID>" + SET_TEXT_COLOR_WHITE + " - a game");
            System.out.println(SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_WHITE + " - when you are done");
            System.out.println(SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_WHITE + " - playing chess");
            System.out.println(SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - with possible commands");
            System.out.println(SET_TEXT_COLOR_BLUE + "clear" + SET_TEXT_COLOR_WHITE + " - DEBUG: clear whole database. REMOVES EVERYTHING!");
        }

    }

    private void drawChessBoard(String perspective) {
        // For now, just a placeholder
        System.out.println("\n--- (Drawing board from " + perspective + " perspective) ---\n");
        System.out.println();

        boardPrinter.draw(perspective);

        System.out.println();
        // new ChessboardPrinter().drawBoard(perspective);
    }
}