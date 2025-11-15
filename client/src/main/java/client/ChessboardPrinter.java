package client;


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

    public void draw(String perspective) {
        boolean isWhitePerspective = perspective.equals("WHITE");

        drawHeader(isWhitePerspective);
        drawRows(isWhitePerspective);
        drawHeader(isWhitePerspective);

        // Reset all formatting
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
    }

    private void drawRows(boolean isWhitePerspective) {
        for (int row = 0; row < 8; row++) {
            // White perspective: 8, 7, ... 1
            // Black perspective: 1, 2, ... 8
            int boardRow = isWhitePerspective ? row : (7 - row);
            int displayRow = isWhitePerspective ? (8 - row) : (row + 1);

            drawRow(boardRow, displayRow, isWhitePerspective);
        }
    }

    private void drawRow(int boardRow, int displayRow, boolean isWhitePerspective) {
        // Draw left row number
        printBorderNumber(displayRow);

        // Draw the 8 squares in the row
        for (int col = 0; col < 8; col++) {
            // White perspective: a, b, ... h (0-7)
            // Black perspective: h, g, ... a (7-0)
            int boardCol = isWhitePerspective ? col : (7 - col);

            // Set square color
            boolean isLightSquare = (displayRow + boardCol) % 2 != 0;
            if (isLightSquare) {
                System.out.print(SET_BG_COLOR_LIGHT_GREY);
            } else {
                System.out.print(SET_BG_COLOR_DARK_GREY);
            }

            // Get and print the piece
            String piece = INITIAL_BOARD[boardRow][boardCol];
            printPiece(piece);
        }

        // Draw right row number
        printBorderNumber(displayRow);

        System.out.println(RESET_BG_COLOR); // Newline
    }

    private void printPiece(String piece) {

        if (Character.isUpperCase(piece.charAt(0))) {
            // Black piece (Blue, as per instructions)
            System.out.print(SET_TEXT_COLOR_BLUE);
        } else {
            // White piece (Red, as per instructions)
            System.out.print(SET_TEXT_COLOR_RED);
        }

        // Pad the piece to fill the square
        System.out.printf(" %s ", piece);
    }

\
    private void printBorderNumber(int displayRow) {
        System.out.print(SET_BG_COLOR_LIGHT_GREY);
        System.out.print(SET_TEXT_COLOR_BLACK);
        System.out.printf(" %d ", displayRow);
    }
}