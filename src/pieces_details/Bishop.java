package pieces_details;

import Interfaces.MoveStrategy;
import game_logic.Board;
import game_logic.Position;
import misc.Enums;
import pieces_details.pieces_moveStrategies.BishopMoveStrategy;

import java.util.ArrayList;
import java.util.List;


public class Bishop extends Piece{
    private MoveStrategy moveStrategy;
    public Bishop(Enums.Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new BishopMoveStrategy();
    }
    public boolean isValidMove(Position from, Position to) {
        int deltaY = to.getY() - from.getY();
        int deltaX = to.getX() - from.getX();
        double tang = (double) deltaX / deltaY;
        if (tang == 1) {
            return true;
        }
        return false;
    }
    public char type() {
        return 'B';
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
