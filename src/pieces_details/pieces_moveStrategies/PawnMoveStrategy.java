package pieces_details.pieces_moveStrategies;

import Interfaces.MoveStrategy;
import game_logic.Board;
import game_logic.Position;
import pieces_details.Pawn;
import pieces_details.Piece;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveStrategy implements MoveStrategy {
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> possibleMoves = new ArrayList<>();
        Position firstMove2row = null;
        Pawn pieceAt = (Pawn) board.getPieceAt(from);
        Position myPosition = pieceAt.getPosition();
        char x = myPosition.getX();
        int y;
        if (myPosition.getY() == pieceAt.getStartRow()) {
            x = myPosition.getX();
            y = myPosition.getY() + 2*pieceAt.getY_axis();
            firstMove2row = new Position(x, y);
        }
        y = myPosition.getY() + pieceAt.getY_axis();
        Position normalMove = new Position(x, y);
        if (firstMove2row != null) {
            if (board.getPieceAt(normalMove) == null &&
                    board.getPieceAt(firstMove2row) == null) {
                possibleMoves.add(firstMove2row);
            }
        }
        if (x >= 'A' && x <= 'H' && y >= 1 && y <= 8) {
            if (board.getPieceAt(normalMove) == null) {
                possibleMoves.add(normalMove);
            }
        }
        int[] OX_beat = {1, -1};
        for(int i = 0; i < 2; i++) {
            x = (char)(myPosition.getX() + OX_beat[i]);
            y = myPosition.getY() + pieceAt.getY_axis();
            if (x >= 'A' && x <= 'H' && y >= 1 && y <= 8) {
                Position beatPosition = new Position(x,y);
                Piece beatPiece = board.getPieceAt(beatPosition);
                if (beatPiece != null && !(beatPiece.getColor().equals(pieceAt.getColor()))) {
                    possibleMoves.add(beatPosition);
                }
            }
        }
        return possibleMoves;
    }
}
