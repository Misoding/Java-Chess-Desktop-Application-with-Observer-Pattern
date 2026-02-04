package misc;

import game_logic.Game;
import game_logic.Move;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import user_details.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.nio.file.Path;
import java.util.*;

public final class JsonWriterUtil {
    JsonWriterUtil() {

    }
    public static void writeAcc(Path path, List<User> users) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Specifica unde trebuie salvat");
        }
        if (users == null) {
            throw new IllegalArgumentException("Nu exista users spre salvare");
        }
        JSONArray accArr = new JSONArray();
        for(User user : users) {
            JSONObject acc = new JSONObject();
            acc.put("email", user.getEmail());
            acc.put("password", user.getPassword());
            acc.put("points", user.getPoints());
            JSONArray games = new JSONArray();
            List<Integer> gameIds = user.getActiveGamesIds();
            if (gameIds != null) {
                for (Integer gameId : gameIds) {
                    games.add(gameId);
                }
            }
            acc.put("games", games);
            accArr.add(acc);
        }
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(accArr.toJSONString());
        }
    }
    public static void writeGames(Path path, Map<Integer, Game> games) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Specifica unde trebuie salvat");
        }
        if (games == null) {
            throw new IllegalArgumentException("Nu exista jocuri spre salvare");
        }
        JSONArray gamesArr = new JSONArray();
        for (Map.Entry<Integer, Game> entry : games.entrySet()) {
            Game game = entry.getValue();
            JSONObject gameObj = new JSONObject();
            gameObj.put("id", game.getGameId());
            JSONArray players = new JSONArray();
            JSONObject playerObj = new JSONObject();
            playerObj.put("email", game.getPlayer().getName());
            playerObj.put("color", game.getPlayer().getPiecesColor().toString());
            playerObj.put("points", game.getPlayer().getPoints());
            players.add(playerObj);

            JSONObject opponent = new JSONObject();
            opponent.put("email", game.getOpponent().getName());
            opponent.put("color", game.getOpponent().getPiecesColor().toString());
            opponent.put("points", game.getOpponent().getPoints());
            players.add(opponent);
            gameObj.put("players", players);
            gameObj.put("currentPlayerColor", game.getCurrentAttacker().getPiecesColor().toString());
            List<Map<String,String>> boardInfo = game.getBoard().transformIntoJson();
            JSONArray boardArray = new JSONArray();
            for (Map<String,String> pieceInfo : boardInfo) {
                JSONObject pieceObj = new JSONObject();
                pieceObj.put("type", pieceInfo.get("type"));
                pieceObj.put("color", pieceInfo.get("color"));
                pieceObj.put("position", pieceInfo.get("position"));
                boardArray.add(pieceObj);
            }
            gameObj.put("board", boardArray);
            JSONArray movesArr = new JSONArray();
            List<Move> moves = game.getGameMoves();
            for (Move move : moves) {
                Map<String, String> moveInfo = move.toJson();
                JSONObject moveObj = new JSONObject();
                moveObj.put("playerColor", moveInfo.get("playerColor"));
                moveObj.put("from", moveInfo.get("from"));
                moveObj.put("to", moveInfo.get("to"));
                movesArr.add(moveObj);
            }
            gameObj.put("moves", movesArr);
            gamesArr.add(gameObj);
        }
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(gamesArr.toJSONString());
        }
    }
    public static void write(Path accPath, Path gamePath, List<User> users, Map<Integer, Game> games)
            throws IOException {
        writeAcc(accPath, users);
        writeGames(gamePath, games);
        System.out.println("Datele au fost salvate cu succes!");
    }
}
