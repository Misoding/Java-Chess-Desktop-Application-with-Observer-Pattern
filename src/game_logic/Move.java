package game_logic;


import misc.Enums;
import pieces_details.Piece;

import java.util.HashMap;
import java.util.Map;

public class Move {
    private Enums.Colors userColor;
    private Position from;
    private Position to;
    private Piece capturedPiece;
    public Move(Enums.Colors color, Position from, Position to, Piece capturedPiece) {
        this.userColor = color;
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
    }
    public Enums.Colors getUserColor() {
        return userColor;
    }
    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }
    public Piece getCapturedPiece() {
        return capturedPiece;
    }
    public Map<String, String> toJson() {
        Map<String, String> moveJson = new HashMap<>();
        moveJson.put("playerColor", this.userColor.toString());
        moveJson.put("from", this.from.toString());
        moveJson.put("to", to.toString());
        return moveJson;
    }
    public String toString() {
        return this.userColor.toString() + "]: " + this.from.toString() + " -> " + this.to.toString() +
                (capturedPiece == null ? "" : "\tCapturat:] " + this.capturedPiece.toString());
    }
}
