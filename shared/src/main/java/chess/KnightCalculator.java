package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightCalculator extends ChessCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[][] directions = {{2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};

        for (int[] dir : directions) {
            addMovesInDirection(board, myPosition, dir[0], dir[1], validMoves);
        }

        return validMoves;
    }

    private void addMovesInDirection(ChessBoard board, ChessPosition startPosition, int rowIncrement, int colIncrement, Collection<ChessMove> validMoves) {
        ChessGame.TeamColor myColor = board.getPiece(startPosition).getTeamColor();

        int nextRow = startPosition.getRow() + rowIncrement;
        int nextCol = startPosition.getColumn() + colIncrement;
        ChessPosition newPosition = new ChessPosition(nextRow, nextCol);

        while (board.isValidPosition(newPosition)) {
            if (board.getPiece(newPosition) == null) {
                validMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            }

            else if (board.getPiece(newPosition).getTeamColor() != myColor) {
                validMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            }

            else {
                break;
            }
        }
    }
}
