package game_logic.game_observers;

import Interfaces.GameObserver;
import game_logic.Move;
import pieces_details.Piece;
import user_details.Player;

public class HistoryObserver implements GameObserver {
    private int moveIndex = 0;
    public void onMoveMade(Move move) {
        this.moveIndex+=1;
        System.out.println("| OBS - HISTORY | ["+this.moveIndex+"] :" + move);
    }
    public void onPieceCaptured(Piece piece) {

    }
    public void onPlayerSwitch(Player currentPlayer) {

    }
}
