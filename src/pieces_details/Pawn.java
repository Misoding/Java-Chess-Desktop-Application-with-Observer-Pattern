package pieces_details;

import Interfaces.MoveStrategy;
import game_logic.Board;
import game_logic.Move;
import game_logic.Position;
import misc.Enums;
import pieces_details.pieces_moveStrategies.PawnMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{
    private int startRow;
    private int y_axis;
    private boolean promoteFlag;
    private MoveStrategy moveStrategy;
    public Pawn(Enums.Colors color, Position position) {
        super(color, position);
        if(color == Enums.Colors.WHITE){
            this.startRow = 2;
            this.y_axis = 1;
        } else {
            this.startRow = 7;
            this.y_axis = -1;
        }
        this.promoteFlag = false;
        this.moveStrategy = new PawnMoveStrategy();
    }
    public char type() {
        return 'P';
    }
    public List<Position> getPossibleMoves(Board board) {
       List<Position> possibleMoves = this.moveStrategy.getPossibleMoves(board, this.getPosition());
        this.setAlreadyVerifiedMoves(possibleMoves);
        return possibleMoves;
    }
    public boolean getPromoteFlag() {
        return this.promoteFlag;
    }
    public void setPromoteFlag(boolean promoteFlag) {
        this.promoteFlag = true;
    }
    public int getStartRow() {
        return this.startRow;
    }
    public int getY_axis() {
        return this.y_axis;
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

