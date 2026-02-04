package pieces_details;

import Interfaces.MoveStrategy;
import game_logic.Board;
import game_logic.ChessPair;
import game_logic.Position;
import misc.Enums;
import pieces_details.pieces_moveStrategies.KingMoveStrategy;

import java.util.*;

public class King extends Piece{
    private MoveStrategy moveStrategy;
    public King(Enums.Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new KingMoveStrategy();
    }
    public char type() {
        return 'K';
    }

    public List<Position> getPossibleMoves(Board board) {
        List<Position> possibleMoves = this.moveStrategy.getPossibleMoves(board, this.getPosition());
        this.setAlreadyVerifiedMoves(possibleMoves);
        return possibleMoves;
    }
    public boolean checkForCheck(Board board, Position kingPos) {
        List<Position> moves = this.getAlreadyVerifiedMoves();
        for (Position move : moves) {
            if (move.equals(kingPos)) {
                return true;
            }
        }
        return false;
    }

}
