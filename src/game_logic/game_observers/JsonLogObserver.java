package game_logic.game_observers;

import Interfaces.GameObserver;
import game_logic.Game;
import game_logic.Move;
import pieces_details.Piece;
import user_details.Player;
import user_details.User;

import java.nio.file.Paths;

import misc.JsonWriterUtil;

import java.util.List;
import java.util.Map;

public class JsonLogObserver implements GameObserver {
    private Game currentGame;
    private User currentUser;
    private Map<Integer, Game> allGames;
    private List<User> allUsers;
    public JsonLogObserver(Game currentGame, User currentUser,
                           Map<Integer, Game> allGames, List<User> allUsers) {
        this.currentGame = currentGame;
        this.currentUser = currentUser;
        this.allGames = allGames;
        this.allUsers = allUsers;
    }
    public void onMoveMade(Move move) {
        this.saveJSON();
    }
    public void onPieceCaptured(Piece piece) {}
    public void onPlayerSwitch(Player currentPlayer) {}
    private void saveJSON() {
        try {
            JsonWriterUtil.write(
                    Paths.get("src/input/accounts.json"),
                    Paths.get("src/input/games.json"),
                    allUsers,
                    allGames
            );
            System.out.println("| OBS - JSON | Miscarea a fost salvata cu success");
        } catch (Exception e) {
            System.err.println("| OBS - JSON | Salvarea a dus la eroare: " + e.getMessage());
        }
    }
}
