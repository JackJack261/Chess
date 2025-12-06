package client;
import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;

public class ChessboardPrinter {

    // Hardcoded initial board state
    // Uppercase = Black pieces, Lowercase = White pieces
    private static final String[][] INITIAL_BOARD = {
            {"R", "N", "B", "Q", "K", "B", "N", "R"}, // Row 8
            {"P", "P", "P", "P", "P", "P", "P", "P"}, // Row 7
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}, // Row 6
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}, // Row 5
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}, // Row 4
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}, // Row 3
            {"p", "p", "p", "p", "p", "p", "p", "p"}, // Row 2
            {"r", "n", "b", "q", "k", "b", "n", "r"}  // Row 1
    };

    // Column labels for each perspective
    private static final String[] COLS_WHITE = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
    private static final String[] COLS_BLACK = {" h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "};

//    public void draw(ChessBoard board, String perspective) {
//        boolean isWhitePerspective = perspective.equals("WHITE");
//
//        drawHeader(isWhitePerspective);
//        drawRows(board, isWhitePerspective);
//        drawHeader(isWhitePerspective);
//
//        // Reset all formatting
//        System.out.println(RESET_BG_COLOR);
//    }

    public void draw(ChessBoard board, String perspective) {
        highlightDraw(board, perspective, null, null);
    }

    public void highlightDraw(ChessBoard board, String perspective, ChessPosition startPosition, Collection<ChessMove> validMoves) {
        boolean isWhitePerspective = perspective.equals("WHITE");

        // Convert moves to a Set of positions for faster lookup
        Set<ChessPosition> highlightTargets = new HashSet<>();
        if (validMoves != null) {
            for (ChessMove move : validMoves) {
                highlightTargets.add(move.getEndPosition());
            }
        }

        drawHeader(isWhitePerspective);
        drawRows(board, isWhitePerspective, startPosition, highlightTargets); // Pass info down
        drawHeader(isWhitePerspective);

        System.out.println(RESET_BG_COLOR);
    }


    private void drawHeader(boolean isWhitePerspective) {
        System.out.print(SET_BG_COLOR_LIGHT_GREY);
        System.out.print(SET_TEXT_COLOR_BLACK);

        System.out.print("   "); // Space for row number
        String[] cols = isWhitePerspective ? COLS_WHITE : COLS_BLACK;
        for (String col : cols) {
            System.out.print(col);
        }
        System.out.print("   "); // Space for row number

        System.out.println(RESET_BG_COLOR); // Newline
        System.out.print(SET_TEXT_COLOR_WHITE);
    }

    private void drawRows(ChessBoard board, boolean isWhitePerspective, ChessPosition startPosition, Set<ChessPosition> targets) {
        for (int row = 0; row < 8; row++) {
            // White perspective: 8, 7, ... 1
            // Black perspective: 1, 2, ... 8
            int boardRow = isWhitePerspective ? (7 - row) : row;
            int displayRow = isWhitePerspective ? (8 - row) : (row + 1);

//            drawRow(board, boardRow, displayRow, isWhitePerspective);
            // new
            drawRow(board, boardRow, displayRow, isWhitePerspective, startPosition, targets);
        }
    }

    private void drawRow(ChessBoard board, int boardRow, int displayRow, boolean isWhitePerspective,
                         ChessPosition startPosition, Set<ChessPosition> targets) {
        // Draw left row number
        printBorderNumber(displayRow);

        // Draw the 8 squares in the row
        for (int col = 0; col < 8; col++) {
            // White perspective: a, b, ... h (0-7)
            // Black perspective: h, g, ... a (7-0)
            int boardCol = isWhitePerspective ? col : (7 - col);

            ChessPosition currentPos = new ChessPosition(boardRow + 1, boardCol + 1);
            boolean isLightSquare = (displayRow + boardCol) % 2 != 0;


            if (startPosition != null && currentPos.equals(startPosition)) {
                System.out.print(SET_BG_COLOR_YELLOW);
            } else if (targets != null && targets.contains(currentPos)) {
                if (isLightSquare) {
                    System.out.print(SET_BG_COLOR_DARK_GREEN);
                } else {
                    System.out.print(SET_BG_COLOR_GREEN);
                }
            } else {
            // Set square color
            if (isLightSquare) {
                System.out.print(SET_BG_COLOR_DARK_GREY);
            } else {
                System.out.print(SET_BG_COLOR_LIGHT_GREY);
            }
            }

            // Get and print the piece
//            String piece = INITIAL_BOARD[boardRow][boardCol];

            var pos = new ChessPosition(boardRow + 1, boardCol + 1);
            ChessPiece piece = board.getPiece(pos);

            printPiece(piece);
        }

        // Draw right row number
        printBorderNumber(displayRow);

        System.out.println(RESET_BG_COLOR); // Newline
    }

    private void printPiece(ChessPiece piece) {

        if (piece == null) {
            System.out.print("   ");
            return;
        }

        if (piece.getTeamColor().toString().equals("BLACK")) {
            // Black piece (Blue, as per instructions)
            System.out.print(SET_TEXT_COLOR_BLUE);
        } else {
            // White piece (Red, as per instructions)
            System.out.print(SET_TEXT_COLOR_RED);
        }

        String symbol = switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };

        // Pad the piece to fill the square
        System.out.printf(" %s ", symbol);
    }

    private void printBorderNumber(int displayRow) {
        System.out.print(SET_BG_COLOR_LIGHT_GREY);
        System.out.print(SET_TEXT_COLOR_BLACK);
        System.out.printf(" %d ", displayRow);
    }
}