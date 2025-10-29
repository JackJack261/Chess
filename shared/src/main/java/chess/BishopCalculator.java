package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

// Variables need a Type, name, initialization, type of object
// Example:
//
//Collection<ChessMove> validMoves;
//validMoves = new HashSet<>();

public class BishopCalculator extends ChessCalculator {

    // This is the main brain of moving a piece! Implement this for each individual piece type.

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // We need to define the directions it can go.
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1,-1}};

        // Loop through the four directions
        // This is a for-each loop, or enhanced for loop
        for (int[] dir : directions) {
            addMovesInDirection(board, myPosition, dir[0], dir[1], validMoves);
        }

        return validMoves;

    }

    // This is the helper function, that does the calculations, checks if its a valid move, and adds it to our array validMoves.

    private void addMovesInDirection(ChessBoard board, ChessPosition startPosition, int rowIncrement, int colIncrement, Collection<ChessMove> validMoves) {
        // First we need to get our team color
        ChessGame.TeamColor myColor = board.getPiece(startPosition).getTeamColor();

        // Since we assume the square we are on is valid, we need to check the next square in line
        // This creates a variable that we can pass to check if it is valid
        int nextRow = startPosition.getRow() + rowIncrement;
        int nextCol = startPosition.getColumn() + colIncrement;
        ChessPosition newPosition = new ChessPosition(nextRow, nextCol);

        // Now we check if this is valid
        while (board.isValidPosition(newPosition)) {
            // square is empty, meaning valid
            if (board.getPiece(newPosition) == null) {
                validMoves.add(new ChessMove(startPosition, newPosition, null));
            }

            // square is occupied by enemy piece
            else if (board.getPiece(newPosition).getTeamColor() != myColor) {
                validMoves.add(new ChessMove(startPosition, newPosition, null));
                break; // stop the while loop
            }

            // square is occupied by friendly piece
            else {
                break; // stop the while loop
            }

            //Finally, we recursively call the next square in line

            nextRow += rowIncrement;
            nextCol += colIncrement;
            newPosition = new ChessPosition(nextRow, nextCol);
        }



    }

}
