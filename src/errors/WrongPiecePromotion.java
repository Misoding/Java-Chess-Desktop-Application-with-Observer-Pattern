package errors;

public class WrongPiecePromotion extends RuntimeException {
    public WrongPiecePromotion() {
        super("You cannot promote to this piece");
    }
    public WrongPiecePromotion(String message) {
        super("You cannot promote to this piece" + message);
    }
}
