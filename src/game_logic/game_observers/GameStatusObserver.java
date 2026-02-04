package game_logic.game_observers;

import Interfaces.GameObserver;
import game_logic.Game;
import game_logic.Move;
import pieces_details.Piece;
import user_details.Player;

import java.util.Observer;

public class GameStatusObserver implements GameObserver {
    private Game game;
    public GameStatusObserver(Game game) {
        this.game = game;
    }
    public void onMoveMade(Move move) {

    }
    public void onPieceCaptured(Piece piece) {

    }
    public void onPlayerSwitch(Player currentPlayer) {
        if (game.getBoard().checkForCheck(currentPlayer.getPiecesColor())) {
            System.out.println("| OBS - STATUS | Jucatorul : " + currentPlayer.getName() + " este in sah");
        }
        if (game.getBoard().advancedCheckForCheck(currentPlayer.getPiecesColor())) {
            System.out.println("| OBS - STATUS | Jucatorul : " + currentPlayer.getName() + " este in sah mah :(");
        }
    }
}
