package pieces_details.pieces_moveStrategies;

import Interfaces.MoveStrategy;
import game_logic.Board;
import game_logic.Position;
import pieces_details.Piece;

import java.util.ArrayList;
import java.util.List;

public class KnightMoveStrategy implements MoveStrategy {
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> possibleMoves = new ArrayList<>();
        // https://prnt.sc/ew5X247XG7-4
        int[] OX = {1,2,2,1,-1,-2,-2,-1};
        int[] OY = {2,1,-1,-2,-2,-1,1,2};
        Piece pieceAt = board.getPieceAt(from);
        Position myPosition = pieceAt.getPosition();
        for(int i = 0; i < 8; i++) {
            char x = (char)(myPosition.getX() + OX[i]);
            int y = (myPosition.getY() + OY[i]);
            if( x < 'A' || x > 'H' || y < 1 || y > 8) {
                continue;
            }
            Piece pieceLocated = board.getPieceAt(new Position(x, y));
            if (pieceLocated == null) {
                possibleMoves.add(new Position(x, y));
            } else {
                if (pieceLocated.getColor().equals(pieceAt.getColor())){
                    continue;
                } else {
                    possibleMoves.add(new Position(x, y));
                    continue;
                }
            }

        }
        return possibleMoves;
    }
}

