package pieces_details;

import errors.WrongPiecePromotion;
import game_logic.Position;
import misc.Enums;

import java.awt.*;

public class PieceFactoryPattern {
    public static Piece createPiece(Enums.PiecesTypes type, Enums.Colors color, Position position) {
        switch (type) {
            case KING:
                return new King(color, position);
            case QUEEN:
                return new Queen(color, position);
            case PAWN:
                return new Pawn(color, position);
            case BISHOP:
                return new Bishop(color, position);
            case ROOK:
                return new Rook(color, position);
            case KNIGHT:
                return new Knight(color, position);
            default:
                return null;
        }
    }
    public static Piece piecePromote(Enums.PiecesTypes type, Enums.Colors color, Position position) {
        if (type == Enums.PiecesTypes.QUEEN || type == Enums.PiecesTypes.ROOK ||
            type == Enums.PiecesTypes.KNIGHT || type == Enums.PiecesTypes.BISHOP) {
            return createPiece(type, color, position);
        }
        throw new WrongPiecePromotion("" + typeToChar(type));
    }
    public static Piece charToPiece(char typeChar, Enums.Colors color, Position position) {
        Enums.PiecesTypes type = charToType(typeChar);
        if (type != null) {
            return createPiece(type, color, position);
        }
        return null;
    }
    public static Enums.PiecesTypes charToType(char typeChar) {
        switch (Character.toUpperCase(typeChar)) {
            case 'K':
                return Enums.PiecesTypes.KING;
            case 'Q':
                return Enums.PiecesTypes.QUEEN;
            case 'P':
                return Enums.PiecesTypes.PAWN;
            case 'B':
                return Enums.PiecesTypes.BISHOP;
            case 'R':
                return Enums.PiecesTypes.ROOK;
            case 'N':
                return Enums.PiecesTypes.KNIGHT;
            default:
                return null;
        }
    }
    public static char typeToChar(Enums.PiecesTypes type) {
        switch (type) {
            case KING:
                return 'K';
            case QUEEN:
                return 'Q';
            case PAWN:
                return 'P';
            case BISHOP:
                return 'B';
            case ROOK:
                return 'R';
            case KNIGHT:
                return 'N';
            default:
                return 'X';
        }
    }
}
