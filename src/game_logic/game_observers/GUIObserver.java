package game_logic.game_observers;

import Interfaces.GameObserver;
import game_logic.Move;
import pieces_details.Piece;
import user_details.Player;

public class GUIObserver implements GameObserver {

    public void onMoveMade(Move move) {
        System.out.println("| GUI - OBSERVER | Modificare tablă după mutare: " + move);
    }

    public void onPieceCaptured(Piece piece) {
        System.out.println("| GUI - OBSERVER |  Actualizare listă piese capturate: " + piece.type());
    }

    public void onPlayerSwitch(Player currentPlayer) {
        System.out.println("| GUI - OBSERVER | Actualizare rând curent: " + currentPlayer.getName());
    }
}