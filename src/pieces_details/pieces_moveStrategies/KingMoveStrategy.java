package pieces_details.pieces_moveStrategies;

import Interfaces.MoveStrategy;
import game_logic.Board;
import game_logic.ChessPair;
import game_logic.Position;
import misc.Enums;
import pieces_details.King;
import pieces_details.Piece;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class KingMoveStrategy implements MoveStrategy {
    public List<Position> calcAllMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        Piece pieceAt =  board.getPieceAt(from);
        Position myPosition = pieceAt.getPosition();
        int[] OX = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] OY = {0, -1, -1, -1, 0, 1, 1, 1};
        for(int i = 0; i < 8;i++) {
            char x = (char)(myPosition.getX() + OX[i]);
            int y = myPosition.getY() + OY[i];
            if (x >= 'A' && x <= 'H' && y >= 1 && y <= 8) {
                Position newPosition = new Position(x, y);
                Piece pieceLocated = board.getPieceAt(newPosition);
                if (pieceLocated == null || !(pieceLocated.getColor().equals(pieceAt.getColor()))){
                    moves.add(newPosition);
                }
            }
        }
        return moves;
    }

    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> movesToVerify = calcAllMoves(board, from);
        List<Position> possibleMoves = new ArrayList<>();
        for (Position move : movesToVerify) {
            boolean positionBeat = false;
            TreeSet<ChessPair<Position, Piece>> boardPieces = board.getBoardPieces();
            List<ChessPair<Position, Piece>> listBoard= new ArrayList<>(boardPieces);
            for (ChessPair<Position, Piece> el : listBoard) {
                Piece oponentPiece = el.getValue();
                Piece kingPiece = board.getPieceAt(from);
                if (oponentPiece.getColor().equals(kingPiece.getColor())) {
                    continue;
                }
                if (oponentPiece.type() == 'K') {
                    List<Position> oponentKingMove = calcAllMoves(board, oponentPiece.getPosition());
                    if (oponentKingMove.contains(move)) {
                        positionBeat = true;
                        break;
                    }
                } else {
                    List<Position> oponentMoves = oponentPiece.getAlreadyVerifiedMoves();
                    if (oponentMoves == null) {
                        oponentMoves = oponentPiece.getPossibleMoves(board);
                    }
                    if (oponentMoves.contains(move)) {
                        positionBeat = true;
                        break;
                    }
                }
            }
            if (!positionBeat) {
                possibleMoves.add(move);
            }
        }
        return possibleMoves;
    }
    }
