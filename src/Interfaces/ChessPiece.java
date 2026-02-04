package Interfaces;

import game_logic.Board;
import game_logic.Position;

import java.util.List;


public interface ChessPiece {
    List<Position> getPossibleMoves(Board board);
    boolean checkForCheck(Board board, Position kingPosition);
    char type();
}
