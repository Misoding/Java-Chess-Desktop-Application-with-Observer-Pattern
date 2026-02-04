package user_details;

import Interfaces.PointsStrategy;
import errors.InvalidMoveException;
import game_logic.Board;
import game_logic.ChessPair;
import game_logic.Position;
import game_logic.game_pointsStrategies.GamePointsStrategies;
import misc.Enums;
import pieces_details.Piece;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Player {
    private String name;
    private Enums.Colors piecesColor;
    private TreeSet<ChessPair<Position, Piece>> userPieces;
    private List<Piece> capturedPieces;
    private int points;
    private PointsStrategy  pointsStrategy;

    public Player() {
        this.userPieces = new TreeSet<>();
        this.capturedPieces = new ArrayList<>();
        this.points = 0;
        this.name = "NULL";
        this.pointsStrategy = new GamePointsStrategies();
    }
    public Player(String name, Enums.Colors color) {
        this.userPieces = new TreeSet<>();
        this.capturedPieces = new ArrayList<>();
        this.name = name;
        this.piecesColor = color;
        this.pointsStrategy = new GamePointsStrategies();

    }
    public String getName() {
        return this.name;
    }
    public boolean setName(String name) {
        if (name == null) {
            return false;
        }
        this.name = name;
        return true;
    }
    public Enums.Colors getPiecesColor() {
        return this.piecesColor;
    }
    public boolean setColor(String color) {
        if(color == null){
            return false;
        }
        try {
            this.piecesColor = Enums.Colors.valueOf(color.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public void setColor(Enums.Colors color) {
        this.piecesColor = color;
    }
    public void makeMove(Position from, Position to, Board board) throws InvalidMoveException {
        if (!board.isValidMove(from,to)) {
            throw new InvalidMoveException("Nu este permisa asa mutare: ["+from+"->"+to+"]");
        }
        Piece pieceFrom = board.getPieceAt(from);
        if (pieceFrom == null) {
            throw new InvalidMoveException("Nu exista acolo piesa");
        }
        if (pieceFrom.getColor() != this.piecesColor){
            throw new InvalidMoveException("Nu poti muta piesele oponentului");
        }
        Piece pieceToCapture = board.getPieceAt(to);
        boolean toCapture = false;
        if (pieceToCapture != null && pieceToCapture.getColor() != this.piecesColor) {
            toCapture = true;
        }

        board.removePiece(to);
        board.removePiece(from);
        pieceFrom.setPosition(to);
        board.placePieceAt(to, pieceFrom);

        if (toCapture) {
            this.capturedPieces.add(pieceToCapture);
//            int piecePoints = this.pieceToPoints(pieceToCapture);
//            this.points += piecePoints;
        }

        board.refreshPieces();
        this.refreshPlayesPieces(board);
    }
    public int pieceToPoints(Piece piece) {
        return this.pointsStrategy.calcPoints(piece);
    }
    public List<Piece> getCapturedPieces() {
        return this.capturedPieces;
    }
    public List<ChessPair<Position, Piece>> getOwnedPieces() {
        return new ArrayList<>(this.userPieces);
    }
    public int getPoints() {
        return this.points;
    }
    public void setPoints(int points) {
        this.points = points > 0 ? points : 0;
    }
    public void setUserPieces(TreeSet<ChessPair<Position, Piece>> userPieces) {
        this.userPieces = userPieces;
    }
    public TreeSet<ChessPair<Position, Piece>> getUserPieces() {
        return this.userPieces;
    }
    public void refreshPlayesPieces(Board board) {
        this.userPieces.clear();
        for (ChessPair<Position, Piece> chessPair : board.getBoardPieces()) {
            Piece piece = chessPair.getValue();
            if (piece.getColor() == this.piecesColor) {
                this.userPieces.add(chessPair);
            }
        }
    }
    public void leaveWinPoints() {
        this.points += this.pointsStrategy.calcPoints("LEAVE");
    }
    public void leaveLosePoints() {
        this.points -= this.pointsStrategy.calcPoints("LEAVE");
        if (this.points < 0) {
            this.points = 0;
        }
    }
    public void winCheckPoints(){
        this.points += this.pointsStrategy.calcPoints("CHECK");
    }
    public void looseCheckPoints(){
        this.points -= this.pointsStrategy.calcPoints("CHECK");
        if (this.points < 0) {
            this.points = 0;
        }
    }
    public Position getKing() {
        for (ChessPair<Position, Piece> chessPair : this.userPieces) {
            if (chessPair.getValue().type() == 'K') {
                return chessPair.getKey();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name + " (" + piecesColor + ")";
    }
}

