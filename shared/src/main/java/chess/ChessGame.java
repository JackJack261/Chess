package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor team;
    private ChessBoard board;
    private boolean isGameOver = false;

    private final java.util.Stack<MoveRecord> moveHistory = new java.util.Stack<>();



    public ChessGame() {
        this.team = TeamColor.WHITE; // default start
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private static class MoveRecord {
        ChessMove move;
        ChessPiece movedPiece;
        ChessPiece capturedPiece;
        TeamColor previousTurn;

        MoveRecord(ChessMove move, ChessPiece movedPiece, ChessPiece capturedPiece, TeamColor previousTurn) {
            this.move = move;
            this.movedPiece = movedPiece;
            this.capturedPiece = capturedPiece;
            this.previousTurn = previousTurn;
        }
    }





    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
//        this.teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
//        throw new RuntimeException("Not implemented");

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return List.of();
        }
        Collection<ChessMove> candidateMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : candidateMoves) {
            // make move
            makeMoveRaw(move);

            // check for check
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

            // unmake move
            unmakeMove(move);
        }

        // debug moves
//        System.out.println("Checking piece at " + board.getPiece(startPosition) + ", valid moves: " + validMoves.size());


        return validMoves;

    }


    private void makeMoveRaw(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPiece captured = board.getPiece(move.getEndPosition());

        MoveRecord record = new MoveRecord(move, piece, captured, team);
        moveHistory.push(record);

        board.removePiece(move.getStartPosition());

        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(move.getEndPosition(), piece);

        team = (team == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (isGameOver) {
            throw new InvalidMoveException("Game is over");
        }

        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (!legalMoves.contains(move)) {
            throw new InvalidMoveException("Illegal move");
        }

        if (board.getPiece(move.getStartPosition()).getTeamColor() != team) {
            throw new InvalidMoveException("Invalid Make Move Out Of Turn");
        }

        makeMoveRaw(move);
    }


    public void unmakeMove(ChessMove move) {

        if (moveHistory.isEmpty()) {
            throw new IllegalStateException("No move to undo");
        }

        MoveRecord record = moveHistory.pop();

        // remove whatever is currently at the end position
        board.removePiece(record.move.getEndPosition());

        // restore the original piece at start position
        board.addPiece(record.move.getStartPosition(), record.movedPiece);

        // restore captured piece if any
        if (record.capturedPiece != null) {
            board.addPiece(record.move.getEndPosition(), record.capturedPiece);
        }

        // restore the previous turn
        team = record.previousTurn;

    }



    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // create variables
        ChessPosition kingPosition = null;
        boolean found = false;
        Collection<ChessMove> enemyMoves = List.of();

        for (int row = 1; row < 9 && !found; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition nextPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(nextPosition);
                if ( piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = nextPosition;
                    found = true;
                    break;
                }
            }
        }

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition enemyPosition = new ChessPosition(row, col);
                ChessPiece enemyPiece = board.getPiece(enemyPosition);
                if (enemyPiece != null && enemyPiece.getTeamColor() != teamColor) {
                    enemyMoves = enemyPiece.pieceMoves(board, enemyPosition);
                }
                for (ChessMove move : enemyMoves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        // check to see if king is in check
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition nextPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(nextPosition);
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }

                Collection<ChessMove> moves = validMoves(nextPosition);
                if (!moves.isEmpty()) {
                    return false; // not checkmate
                }
            }
        }

        isGameOver = true;
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // check to see if king is in check


        if (isInCheck(teamColor)) {
            return false;
        }

        // Check every piece on the board
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // skip pieces that are null or enemy color
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                // add validMoves to a new collection and test if it is empty
                Collection<ChessMove> moves = validMoves(position);
                if (!moves.isEmpty()) {
                    return false;
                }
            }
        }
        isGameOver = true;
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }


    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(board, chessGame.board) && Objects.equals(moveHistory, chessGame.moveHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, board, moveHistory);
    }

}
