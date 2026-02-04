package Interfaces;

import game_logic.Move;
import pieces_details.Piece;
import user_details.Player;

public interface GameObserver {
    void onMoveMade(Move move);
    void onPieceCaptured(Piece piece);
    void onPlayerSwitch(Player currentPlayer);
}
