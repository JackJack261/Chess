package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        // throw new RuntimeException("Not implemented");
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {

//        throw new RuntimeException("Not implemented");
        // Adding comment
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     *
     * This is what returns all possible moves that I need to calculate
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        throw new RuntimeException("Not implemented");
        ChessPiece piece = board.getPiece(myPosition);

        if  (piece.getPieceType() == PieceType.BISHOP){
            return new BishopCalculator().PieceMoves(board, myPosition);
        }

        else if (piece.getPieceType() == PieceType.ROOK) {
            return new RookCalculator().PieceMoves(board, myPosition);
        }

        else if (piece.getPieceType() == PieceType.QUEEN) {
            Collection<ChessMove> queenMoves = new BishopCalculator().PieceMoves(board, myPosition);
            queenMoves.addAll(new RookCalculator().PieceMoves(board, myPosition));
            return queenMoves;
        }

        else if (piece.getPieceType() == PieceType.KING) {
            return ???;
        }



        return List.of();
    }
}
