package game_logic;

import misc.Enums;
import pieces_details.Piece;
import pieces_details.PieceFactoryPattern;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Board {
    private TreeSet<ChessPair<Position, Piece>> asociatedPositions;
    private Enums.Colors colorView;

    public Board() {
        this.asociatedPositions = new TreeSet<>();
        this.colorView = Enums.Colors.WHITE;
    }
    public void initialize() {
        asociatedPositions.clear();
        Map<String, Enums.PiecesTypes> pieces = createDistributionPieces();
        for (Map.Entry<String, Enums.PiecesTypes> mapElement : pieces.entrySet()) {
            Position whitePosition = extractPosition(mapElement.getKey());
            Position blackPosition = inversePosition(whitePosition);
            Piece whitePiece = PieceFactoryPattern.createPiece(mapElement.getValue(),
                    Enums.Colors.WHITE, whitePosition);
            Piece blackPiece = PieceFactoryPattern.createPiece(mapElement.getValue(),
                    Enums.Colors.BLACK, blackPosition);
            asociatedPositions.add(new ChessPair<>(whitePosition, whitePiece));
            asociatedPositions.add(new ChessPair<>(blackPosition, blackPiece));
        }
        refreshPieces();
    }
    public void initializeJSONvers(List<Map<String, String>> boardInfo) {
        asociatedPositions.clear();
        for(Map<String, String> pieceDescriptors : boardInfo) {
            String typeRaw = pieceDescriptors.get("type");
            String colorRaw = pieceDescriptors.get("color");
            String positionRaw = pieceDescriptors.get("position");
            if (typeRaw == null ||  colorRaw == null || positionRaw == null) {
                continue;
            }
            char type = typeRaw.charAt(0);
            Enums.Colors color = Enums.Colors.valueOf(colorRaw.toUpperCase());
            Position position = extractPosition(positionRaw);
            Piece piece = PieceFactoryPattern.charToPiece(type, color, position);
            if (piece != null) {
                asociatedPositions.add(new ChessPair(position, piece));
            }
        }
    }
    private Position extractPosition(String positionRaw) {
        char x =  positionRaw.charAt(0);
        int y = Character.getNumericValue(positionRaw.charAt(1));
        return new Position(x, y);
    }
    public Position inversePosition(Position position) {
        char x = position.getX();
        int y = 9 - position.getY();
        return new Position(x, y);
    }
    public void promotePiece(Position position, Enums.PiecesTypes pieceType, Enums.Colors color) {
        Piece pieceToPromote = this.getPieceAt(position);
        if (pieceToPromote == null || pieceToPromote.type() != 'P') {
            return;
        }
        Piece promoted = PieceFactoryPattern.piecePromote(pieceType, color, position);
        this.removePiece(position);
        this.placePieceAt(position, promoted);
        this.refreshPieces();
    }

    public boolean legitPromovation(Position position) {
        Piece piece = this.getPieceAt(position);
        if (piece == null || piece.type() != 'P') {
            return false;
        }
        if (piece.getColor() == Enums.Colors.WHITE && position.getY() == 8) {
            return true;
        }
        if (piece.getColor() == Enums.Colors.BLACK && position.getY() == 1) {
            return true;
        }
        return false;
    }
    private Map<String, Enums.PiecesTypes> createDistributionPieces() {
        Map<String, Enums.PiecesTypes> distribution = new HashMap<>();
        distribution.put("A1", Enums.PiecesTypes.ROOK);
        distribution.put("B1", Enums.PiecesTypes.KNIGHT);
        distribution.put("C1", Enums.PiecesTypes.BISHOP);
        distribution.put("D1", Enums.PiecesTypes.QUEEN);
        distribution.put("E1", Enums.PiecesTypes.KING);
        distribution.put("F1", Enums.PiecesTypes.BISHOP);
        distribution.put("G1", Enums.PiecesTypes.KNIGHT);
        distribution.put("H1", Enums.PiecesTypes.ROOK);
        for (char x = 'A'; x <= 'H'; x++) {
            distribution.put(x + "2", Enums.PiecesTypes.PAWN);
        }
        return distribution;
    }
    public List<Map<String, String>> transformIntoJson() {
        List<Map<String, String>> boardInfo = new ArrayList<>();
        for (ChessPair<Position, Piece> chessPair : this.asociatedPositions) {
            Position position = chessPair.getKey();
            Piece piece = chessPair.getValue();
            Map<String, String> pieceInfo = new HashMap<>();
            pieceInfo.put("type", String.valueOf(piece.type()));
            pieceInfo.put("color", piece.getColor().toString());
            pieceInfo.put("position", String.valueOf(position));
            boardInfo.add(pieceInfo);
        }
        return boardInfo;
    }
    public Enums.Colors getColorView() {
        return this.colorView;
    }
    public void setColorView(Enums.Colors colorView) {
        this.colorView = colorView;
    }
    public TreeSet<ChessPair<Position, Piece>> getBoardPieces() {
        return this.asociatedPositions;
    }
    public Piece getPieceAt(Position position) {
        for (ChessPair<Position, Piece> chessPair : this.asociatedPositions) {
            if (chessPair.getKey().equals(position)) {
                return chessPair.getValue();
            }
        }
        return null;
    }
    public void removePiece(Position position) {
        ChessPair<Position, Piece> toRemove = null;
        for (ChessPair<Position, Piece> chessPair : this.asociatedPositions) {
            if(chessPair.getKey().equals(position)) {
                toRemove = chessPair;
                break;
            }
        }
        if (toRemove != null) {
            this.asociatedPositions.remove(toRemove);
        }
    }
    public void placePieceAt(Position position, Piece piece) {
        removePiece(position);
        this.asociatedPositions.add(new ChessPair(position, piece));
    }
    public void refreshPieces() {
        for (ChessPair<Position, Piece> chessPair : this.asociatedPositions) {
            Piece piece = chessPair.getValue();
            piece.setAlreadyVerifiedMoves(piece.getPossibleMoves(this));
        }
    }
    public boolean isValidMove(Position from, Position to) {
        Piece piece = getPieceAt(from);
        if (piece == null) {
            return false;
        }
        List<Position> possibleMoves = piece.getPossibleMoves(this);
        boolean found = possibleMoves.contains(to);
        if (!found) {
            return false;
        }
        Piece beatPiece = getPieceAt(to);
        removePiece(to);
        removePiece(from);
        piece.setPosition(to);
        this.asociatedPositions.add(new ChessPair(to, piece));
        boolean checkFlag = this.checkForCheck(piece.getColor());
        removePiece(to);
        piece.setPosition(from);
        this.asociatedPositions.add(new ChessPair(from, piece));
        if (beatPiece != null) {
            asociatedPositions.add(new ChessPair(to, beatPiece));
        }
        return !checkFlag;
    }
    public Position getKing(Enums.Colors color) {
        for (ChessPair<Position, Piece> chessPair : this.asociatedPositions) {
            Piece piece = chessPair.getValue();
            if (piece.type() == 'K' && piece.getColor() == color) {
                return piece.getPosition();
            }
        }
        return null;
    }
    public boolean checkForCheck(Enums.Colors color) {
        Position kingPosition = getKing(color);
        if (kingPosition == null) {
            return false;
        }
        for (ChessPair<Position, Piece> chessPair : this.asociatedPositions) {
            Piece piece = chessPair.getValue();
            if (piece.getColor() != color) {
                List<Position> boardCurMoves = piece.getPossibleMoves(this);
                if (boardCurMoves.contains(kingPosition)) {
                    return true;
                }
                // if (piece.checkForCheck(this, kingPosition)) {
                //     return true;
                // }
            }
        }
        return false;
    }
    public boolean advancedCheckForCheck(Enums.Colors color) {
        if (!checkForCheck(color)){
            return false;
        }
        List<ChessPair<Position, Piece>> piecesList = new ArrayList<>(asociatedPositions);
        for (ChessPair<Position, Piece> chessPair : piecesList) {
            Piece piece = chessPair.getValue();
            if (piece.getColor() == color) {
                List<Position> possibleMoves = piece.getPossibleMoves(this);
                for (Position possibleMove : possibleMoves) {
                    if (isValidMove(piece.getPosition(), possibleMove)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public void display() {
        if (this.colorView == Enums.Colors.WHITE) {
            this.displayWhite();
        } else {
            this.displayBlack();
        }
    }
    private void displayWhite() {
        for (int y = 8; y >= 1; y--) {
            System.out.print(y + "|");
            for (char x = 'A'; x <= 'H'; x++) {
                displayPiece(new Position(x,y));
                System.out.print("|");
            }
            System.out.println();
        }
        System.out.println("  A  B  C  D  E  F  G  H");
    }
    private void displayBlack() {
        for (int y = 1; y <= 8; y++) {
            System.out.print(y + "|");
            for (char x = 'A'; x <= 'H'; x++) {
                displayPiece(new Position(x,y));
                System.out.print("|");
            }
            System.out.println();
        }
        System.out.println("  A  B  C  D  E  F  G  H");
    }
    private void displayPiece(Position position) {
        Piece piece = getPieceAt(position);
        if (piece == null) {
            System.out.print("  ");
        } else {
            char pieceChar = piece.type();
            char colorChar = (piece.getColor() == Enums.Colors.WHITE) ? 'W' : 'B';
            System.out.print(pieceChar + "" + colorChar);
        }
    }
    public TreeSet<ChessPair<Position, Piece>> getPlayerPieces(Enums.Colors color) {
        TreeSet<ChessPair<Position, Piece>> playerPieces = new TreeSet<>();
        for (ChessPair<Position, Piece> chessPair : this.asociatedPositions) {
            if (chessPair.getValue().getColor() == color) {
                playerPieces.add(chessPair);
            }
        }
        return playerPieces;
    }
    public int getPiecesRemains() {
        return this.asociatedPositions.size();
    }
    public List<Piece> getPiecesColored(Enums.Colors color) {
        ArrayList<Piece> piecesColored = new ArrayList<>();
        for(ChessPair<Position,Piece> chessPair : this.asociatedPositions) {
            if (chessPair.getValue().getColor() == color) {
                piecesColored.add(chessPair.getValue());
            }
        }
        return piecesColored;
    }
}
