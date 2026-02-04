package Interfaces;

import game_logic.Board;
import game_logic.Position;

import java.util.List;

public interface MoveStrategy {
    List<Position> getPossibleMoves(Board board, Position from);
}
