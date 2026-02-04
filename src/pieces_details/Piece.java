package pieces_details;

import Interfaces.ChessPiece;
import game_logic.ChessPair;
import game_logic.Position;
import misc.Enums;

import java.util.List;

public abstract class Piece implements ChessPiece {
    private final Enums.Colors color;
    private Position position;
    private List<Position> alreadyVerifiedMoves;
    public Piece (Enums.Colors color, Position position) {
        this.position = position;
        this.color = color;
    }
    public Enums.Colors getColor() {
        return this.color;
    }
    public Position getPosition() {
        return this.position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    public List<Position> getAlreadyVerifiedMoves() {
        return this.alreadyVerifiedMoves;
    }
    public void setAlreadyVerifiedMoves(List<Position> possibleMoves) {
        this.alreadyVerifiedMoves = possibleMoves;
    }
}
