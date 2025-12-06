package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import dataaccess.DataAccessException;
import models.*;
import requestsandresults.GameInfo;
import client.ChessboardPrinter;
import ui.EscapeSequences.*;

import client.websocket.NotificationHandler;
import client.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import chess.ChessGame;

import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client implements NotificationHandler {

    private final ServerFacade serverFacade;
    private boolean isLoggedIn = false;
    private String authToken = null;
    private List<GameInfo> displayedGames;
    private final ChessboardPrinter boardPrinter;
    private WebSocketFacade ws;
    private final String serverUrl;
    private String visitorColor = "WHITE";
    private int currentGameID;

    private ChessGame currentGame;

    public Client(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.boardPrinter = new ChessboardPrinter();
        this.serverUrl = serverUrl;
    }

    private void printPrompt() {
        if (isLoggedIn) {
            System.out.print("\n[LOGGED_IN] >>> ");
        } else {
            System.out.print("\n[LOGGED_OUT] >>> ");
        }
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("♕ Welcome to Chess. Type 'help' to get started.");

        while (true) {
            printPrompt();

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

    private void handlePostloginCommands(String command, String[] args) throws Exception {
        System.out.println("Post-login command: " + command);


        // Logout
        if (command.equals("logout")) {
            if (args.length == 1) {

                serverFacade.logout(authToken);

                this.isLoggedIn = false;
                this.authToken = null;
                this.displayedGames = null;

                if (ws != null) {
                    ws.session.close();
                    ws = null;
                }

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

                    if (ws == null) {
                        try {
                            ws = new WebSocketFacade(serverUrl, this);
                        } catch (Exception e) {
                            System.out.println("Failed to connect to WebSocket: " + e.getMessage());
                            return;
                        }
                    }

                    this.visitorColor = playerColor;
                    this.currentGameID = gameNumber;

                    serverFacade.joinGame(authToken, playerColor, game.gameID());
                    System.out.println("Joined game as " + playerColor + ".");

                    try {
                        var wsCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, game.gameID());
                        ws.sendCommand(wsCommand);
                    } catch (Exception e) {
                        System.out.println("Failed to send CONNECT command: " + e.getMessage());
                    }

//                    drawChessBoard(playerColor);

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

                    if (ws == null) {
                        try {
                            ws = new WebSocketFacade(serverUrl, this);
                        } catch (Exception e) {
                            System.out.println("Failed to connect to WebSocket: " + e.getMessage());
                            return;
                        }
                    }

                    this.visitorColor = "WHITE";
                    this.currentGameID = gameNumber;

                    try {
                        var wsCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, game.gameID());
                        ws.sendCommand(wsCommand);
                    } catch (Exception e) {
                        System.out.println("Failed to send CONNECT command: " + e.getMessage());
                    }

                    System.out.println("Observing game.");

                } catch (NumberFormatException e) {
                    System.out.println("Error: Game ID must be a number.");
                }
            } else {
                System.out.println("Usage: observe game <ID>");
            }
        }


        else if (command.equals("move")) {
            if (args.length >= 3) {
                try {
                    String startStr = args[1];
                    String endStr = args[2];
                    String promotionPieceStr = (args.length > 3) ? args[3] : null;

                    ChessPosition startPos = convertToPosition(startStr);
                    ChessPosition endPos = convertToPosition(endStr);
                    ChessPiece.PieceType promotionPiece = convertPromotionPiece(promotionPieceStr);

                    ChessMove move = new ChessMove(startPos, endPos, promotionPiece);

                    if (ws == null) {
                        System.out.println("Error: You are not connected to a game.");
                        return;
                    }

                    ws.sendCommand(new MakeMoveCommand(authToken, currentGameID, move));

                    System.out.println("Sending move: " + startStr + " to " + endStr);


                } catch (NumberFormatException e) {
                    System.out.println("Invalid coordinates. Usage: move <START> <END> [PROMOTION]");
                    System.out.println("Example: move e2 e4");
                } catch (Exception e) {
                    System.out.println("Error processing move: " + e.getMessage());
                }
            }

            else {
                System.out.println("Usage: move <START> <END> [PROMOTION_PIECE]");
            }
        }


        // Leave
        else if (command.equals("leave")) {
            if (args.length == 1) {
                if (ws == null) {
                    System.out.println("Error: You are not connected to a game.");
                    return;
                }

                try {
                    ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, currentGameID));

                    ws.session.close();
                    ws = null;

                    this.currentGameID = -1;
                    this.visitorColor = null;

                    System.out.println("Left the game.");

                } catch (Exception e) {
                    System.out.println("Error leaving game: " + e.getMessage());
                }
            } else {
                System.out.println("Usage: leave");
            }
        }

        // Resign
        else if (command.equals("resign")) {
            if (args.length == 1) {
                if (ws == null) {
                    System.out.println("Error: You are not connected to a game.");
                    return;
                }

                try {
                    System.out.print("Are you sure you want to resign? (yes/no): ");
                    String input = new Scanner(System.in).nextLine();

                    if (input.equalsIgnoreCase("yes")) {
                        ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, currentGameID));
                    } else {
                        System.out.println("Resignation cancelled.");
                    }

                } catch (Exception e) {
                    System.out.println("Error resigning: " + e.getMessage());
                }
            } else {
                System.out.println("Usage: resign");
            }
        }

        // Redraw
        else if (command.equals("redraw")) {
            if (currentGame != null) {
                boardPrinter.draw(currentGame.getBoard(), this.visitorColor);
            } else {
                System.out.println("No active game to redraw.");
            }
        }


        // Highlight
        else if (command.equals("highlight")) {
            if (args.length == 2) {
                try {
                    ChessPosition startPos = convertToPosition(args[1]);

                    if (currentGame == null) {
                        System.out.println("No active game.");
                        return;
                    }

                    var validMoves = currentGame.validMoves(startPos);

                    if (validMoves == null || validMoves.isEmpty()) {
                        System.out.println("No legal moves for that piece.");
                    } else {
//                         boardPrinter.highlightDraw(currentGame.getBoard(), this.visitorColor, validMoves);
                        boardPrinter.highlightDraw(currentGame.getBoard(), this.visitorColor, startPos, validMoves);
                        System.out.println("Highlighting moves for " + args[1] + "...");
                    }

                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Usage: highlight <POSITION> (e.g., highlight e2)");
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
            System.out.println(SET_TEXT_COLOR_BLUE + "move <start position> <end position> <promotion piece (for pawns), default is null>" + SET_TEXT_COLOR_WHITE + " - move a piece");
            System.out.println(SET_TEXT_COLOR_BLUE + "highlight <chess position>" + SET_TEXT_COLOR_WHITE + " - show legal moves");
            System.out.println(SET_TEXT_COLOR_BLUE + "redraw" + SET_TEXT_COLOR_WHITE + " - redraw the chess board");
            System.out.println(SET_TEXT_COLOR_BLUE + "observe <ID>" + SET_TEXT_COLOR_WHITE + " - a game");
            System.out.println(SET_TEXT_COLOR_BLUE + "leave" + SET_TEXT_COLOR_WHITE + " - leave a game");
            System.out.println(SET_TEXT_COLOR_BLUE + "resign" + SET_TEXT_COLOR_WHITE + " - forfeit a game");
            System.out.println(SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_WHITE + " - when you are done");
            System.out.println(SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_WHITE + " - playing chess");
            System.out.println(SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - with possible commands");
            System.out.println(SET_TEXT_COLOR_BLUE + "clear" + SET_TEXT_COLOR_WHITE + " - DEBUG: clear whole database. REMOVES EVERYTHING!");
        }

    }


    private ChessPiece.PieceType convertPromotionPiece(String piece) {
        if (piece == null) {
            return null;
        }
        return switch (piece.toUpperCase()) {
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            default -> null;
        };
    }

    private ChessPosition convertToPosition(String positionText) throws NumberFormatException {
        // takes something like "a3" and converts it to [0, 4]
        if (positionText.length() != 2) {
            throw new NumberFormatException("Invalid coordinate length");
        }

        char colChar = positionText.toLowerCase().charAt(0);
        char rowChar = positionText.charAt(1);

        // Convert column to 1-8
        int col = colChar - 'a' + 1;
        // Convert row to 1-8
        int row = Character.getNumericValue(rowChar);


        // Dang board is inverted, this should fix it

        // DEBUG
//        System.out.println("DEBUG BEFORE: Row: " + row + ", Col: " + col);

//        row = 9 - row;

//        System.out.println("DEBUG AFTER: Row: " + row + ", Col: " + col);


        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new NumberFormatException("Coordinate out of bounds");
        }

        return new ChessPosition(row, col);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGame = (LoadGameMessage) message;
                ChessGame game = loadGame.getGame();

                System.out.println(ERASE_LINE + "\r");

                this.currentGame = loadGame.getGame();

                boardPrinter.draw(game.getBoard(), this.visitorColor);

                printPrompt();

                // Debug
//                System.out.println("Received LOAD_GAME");
            }
            case ERROR -> {
                ErrorMessage error = (ErrorMessage) message;
                System.out.println();
                System.out.println(SET_TEXT_COLOR_RED + "Error: " + error.getErrorMessage() + SET_TEXT_COLOR_WHITE);
                printPrompt();
            }
            case NOTIFICATION -> {
                NotificationMessage notification = (NotificationMessage) message;
                System.out.println();
                System.out.println(SET_TEXT_COLOR_BLUE + notification.getMessage() + SET_TEXT_COLOR_WHITE);
                printPrompt();
            }
        }
    }
}