package pieces_details.pieces_moveStrategies;

import Interfaces.MoveStrategy;
import game_logic.Board;
import game_logic.Position;
import pieces_details.Piece;

import java.util.ArrayList;
import java.util.List;

public class BishopMoveStrategy implements MoveStrategy {
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> possibleMoves = new ArrayList<>();
        int[] OX = {1, 1, -1, -1};
        int[] OY = {1, -1, -1, 1};
        Piece pieceAt = board.getPieceAt(from);
        Position myPosition = pieceAt.getPosition();
        // 1 1 -> /
        // 1 -1 -> \
        // -1 -1 -> /
        // -1, 1 \
        // https://prnt.sc/D7j7Ota63u2z
        for(int i = 0; i < 4; i++) {
            for(int add = 1; add < 8; add++) {
                char x = (char)(myPosition.getX() + add * OX[i]);
                int y = myPosition.getY() + add * OY[i];
                if (x < 'A' || x > 'H' || y < 1 || y > 8) {
                    break;
                }
                Piece pieceLocated = board.getPieceAt(new Position(x,y));
                if (pieceLocated == null) {
                    possibleMoves.add(new Position(x,y));
                } else {
                    if (pieceLocated.getColor().equals(pieceAt.getColor())){
                        break;
                    } else {
                        possibleMoves.add(new Position(x,y));
                        break;
                    }
                }
            }
        }
        return possibleMoves;
    }
}
