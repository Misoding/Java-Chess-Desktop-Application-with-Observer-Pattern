package game_logic.game_observers;

import Interfaces.GameObserver;
import game_logic.Game;
import game_logic.Move;
import misc.Enums;
import pieces_details.Piece;
import user_details.Player;
import user_details.User;

import java.awt.*;

public class PointsObserver implements GameObserver {
    private int whitePoints = 0;
    private int blackPoints = 0;
    private Game game;
    private User user;
    public PointsObserver(Game game, User user) {
        this.game = game;
        this.user = user;

    }
    public void onMoveMade(Move move) {

    }
    public void onPieceCaptured(Piece piece){
        int points = this.getValue(piece);

        Player whitePlayer = (game.getPlayer().getPiecesColor() == Enums.Colors.WHITE) ? game.getPlayer() : game.getOpponent();
        Player blackPlayer = (game.getPlayer().getPiecesColor() == Enums.Colors.BLACK) ? game.getPlayer() : game.getOpponent();


        if (piece.getColor() == Enums.Colors.WHITE) {
            blackPlayer.setPoints(blackPlayer.getPoints() + points);
            this.blackPoints += points;
            if (blackPlayer.getName().equals(this.user.getEmail())) {
                user.setPoints(user.getPoints() + points);
            }
            System.out.println("| OBS - POINTS | Negru primeste + "+points+", Total: " + game.getOpponent().getPoints());
        } else {
            this.whitePoints += points;
            whitePlayer.setPoints(whitePlayer.getPoints() + points);

            if (whitePlayer.getName().equals(this.user.getEmail())) {
                user.setPoints(user.getPoints() + points);
            }
            System.out.println("| OBS - POINTS | Albul primeste + "+points+", Total: " + whitePlayer.getPoints());
        }
    }
    public void onPlayerSwitch(Player currentPlayer) {

    }
    private int getValue(Piece piece) {
        switch (piece.type()) {
            case 'Q': return 90;
            case 'R': return 50;
            case 'B': return 30;
            case 'N': return 30;
            case 'P': return 10;
            default: return 0;
        }
    }
}
