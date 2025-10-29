package chess;

import java.util.Collection;

public abstract class ChessCalculator {
    // Public scope: Return array or collection of chess moves
    // get PieceType

    // Each method needs a scope, return value, name, parameters


    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
