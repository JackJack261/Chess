package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalculator extends ChessCalculator{
    @Override
    public Collection<ChessMove> PieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

        if (myColor == ChessGame.TeamColor.WHITE){
            int[][] directions = {{1, 0}};
            for (int[] dir : directions) {
                addMovesInDirection(board, myPosition, dir[0], dir[1], validMoves, myColor);
            }
        }

        else {
            int[][] directions = {{-1, 0}};
            for (int[] dir : directions) {
                addMovesInDirection(board, myPosition, dir[0], dir[1], validMoves, myColor);
            }
        }

        return validMoves;
    }

    private void addMovesInDirection(ChessBoard board, ChessPosition startPosition, int rowIncrement, int colIncrement, Collection<ChessMove> validMoves, ChessGame.TeamColor myColor) {

        // Position in front of pawn
        int nextRow = startPosition.getRow() + rowIncrement;
        int nextCol = startPosition.getColumn() + colIncrement;
        ChessPosition newPosition = new ChessPosition(nextRow, nextCol);

        // Position 2 in front of pawn
        int firstMoveRowWhite = startPosition.getRow() + 2;
        int firstMoveRowBlack = startPosition.getRow() - 2;

        ChessPosition firstMovePositionWhite = new ChessPosition(firstMoveRowWhite, nextCol);
        ChessPosition firstMovePositionBlack = new ChessPosition(firstMoveRowBlack, nextCol);

        // Position to the diagonal of pawn
        int checkCaptureRowWhite = startPosition.getRow() + 1;
        int checkCaptureRowBlack = startPosition.getRow() - 1;

        int checkCaptureColPos = startPosition.getColumn() + 1;
        int checkCaptureColNeg = startPosition.getColumn() - 1;

        ChessPosition capturePositionNeg = null;
        ChessPosition capturePositionPos = null;

        if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            capturePositionPos = new ChessPosition(checkCaptureRowWhite, checkCaptureColPos);
            capturePositionNeg = new ChessPosition(checkCaptureRowWhite, checkCaptureColNeg);
        }

        else {
            capturePositionPos = new ChessPosition(checkCaptureRowBlack, checkCaptureColPos);
            capturePositionNeg = new ChessPosition(checkCaptureRowBlack, checkCaptureColNeg);
        }

        // Checking and adding moves
        
        // square is empty
        if (board.isValidPosition(newPosition) && board.getPiece(newPosition) == null) {
            validMoves.add(new ChessMove(startPosition, newPosition, null));
        }

        // check if pawn is at starting position

        if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.WHITE && startPosition.getRow() == 2 && board.isValidPosition(firstMovePositionWhite) && board.getPiece(newPosition) == null && board.getPiece(firstMovePositionWhite) == null) {
            validMoves.add(new ChessMove(startPosition, firstMovePositionWhite, null));
        }

        if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK && startPosition.getRow() == 7 && board.isValidPosition(firstMovePositionBlack) && board.getPiece(newPosition) == null && board.getPiece(firstMovePositionBlack) == null) {
            validMoves.add(new ChessMove(startPosition, firstMovePositionBlack, null));
        }

        // pawn can capture enemy piece Pos
        if (board.isValidPosition(capturePositionPos) && board.getPiece(capturePositionPos) != null && board.getPiece(capturePositionPos).getTeamColor() != myColor) {

            if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.WHITE && startPosition.getRow() == 7) {
                validMoves.add(new ChessMove(startPosition, capturePositionPos, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(startPosition, capturePositionPos, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(startPosition, capturePositionPos, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(startPosition, capturePositionPos, ChessPiece.PieceType.KNIGHT));
                validMoves.remove(new ChessMove(startPosition, capturePositionPos, null));
            }

            else if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK && startPosition.getRow() == 2) {
                validMoves.add(new ChessMove(startPosition, capturePositionPos, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(startPosition, capturePositionPos, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(startPosition, capturePositionPos, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(startPosition, capturePositionPos, ChessPiece.PieceType.KNIGHT));
                validMoves.remove(new ChessMove(startPosition, capturePositionPos, null));
            }
            else {
                validMoves.add(new ChessMove(startPosition, capturePositionPos, null));
            }
        }

        // pawn can capture enemy piece Neg
        if (board.isValidPosition(capturePositionNeg) && board.getPiece(capturePositionNeg) != null && board.getPiece(capturePositionNeg).getTeamColor() != myColor) {
            if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.WHITE && startPosition.getRow() == 7) {
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, ChessPiece.PieceType.KNIGHT));
                validMoves.remove(new ChessMove(startPosition, capturePositionNeg, null));
            }

            else if (board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK && startPosition.getRow() == 2) {
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, ChessPiece.PieceType.KNIGHT));
                validMoves.remove(new ChessMove(startPosition, capturePositionNeg, null));
            }
            else {
                validMoves.add(new ChessMove(startPosition, capturePositionNeg, null));
            }
        }

        // promotion White

        if (startPosition.getRow() == 7 && board.isValidPosition(newPosition) && board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            validMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.KNIGHT));
            validMoves.remove(new ChessMove(startPosition, newPosition, null));
        }

        // promotion Black

        if (startPosition.getRow() == 2 && board.isValidPosition(newPosition) && board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            validMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(startPosition, newPosition, ChessPiece.PieceType.KNIGHT));
            validMoves.remove(new ChessMove(startPosition, newPosition, null));
        }

    }
}
