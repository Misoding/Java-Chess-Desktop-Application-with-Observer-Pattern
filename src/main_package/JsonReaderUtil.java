package main_package;

import game_logic.*;
import misc.Enums;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pieces_details.Piece;
import pieces_details.PieceFactoryPattern;
import user_details.Player;
import user_details.User;

import java.util.Map;

/**
 * Expected structures:
 * - accounts.json: an array of objects with fields: email (String), password (String), points (Number), games (array of numbers)
 * - games.json: an array of objects with fields matching the JSON provided:
 *   id (Number), players (array of {email, color}), currentPlayerColor (String),
 *   board (array of {type, color, position}), moves (array of {playerColor, from, to})
 */
public final class JsonReaderUtil {

    private JsonReaderUtil() {
    }

    /**
     * Reads the accounts from the given JSON file path.
     *
     * @param path path to accounts.json
     * @return list of Account objects (empty list if file not found or array empty)
     * @throws IOException    if I/O fails
     * @throws ParseException if JSON is invalid
     */


    public static List<User> readAccounts(Path path) throws IOException, ParseException {
        if (path == null || !Files.exists(path)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            List<User> result = new ArrayList<>();

            if (arr == null) {
                return result;
            }

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) {
                    continue;
                }

                User user = new User();
                String email = asString(obj.get("email"));

                String password = asString(obj.get("password"));
                if (!user.setEmail(email)) {
                    System.out.println("Wrong email format [" + email + "]");
                    continue;
                }
                if (!user.setPassword(password)) {
                    System.out.println("Wrong password format [" + password + "]");
                    continue;
                }
                user.setPoints(asInt(obj.get("points"), 0));
                List<Integer> gameIds = new ArrayList<>();
                JSONArray games = asArray(obj.get("games"));
                if (games != null) {
                    for (Object gid : games) {
                        gameIds.add(asInt(gid, 0));
                    }
                }
                user.setUserActiveGamesIds(gameIds);
                result.add(user);
            }
            return result;
        }
    }

    /**
     * Reads the games from the given JSON file path and returns them as a map by id.
     * The structure strictly follows games.json as provided (no title/genre).
     *
     * @param path path to games.json
     * @return map id -> Game (empty if file missing or array empty)
     * @throws IOException    if I/O fails
     * @throws ParseException if JSON is invalid
     */
    public static Map<Integer, Game> readGamesAsMap(Path path) throws IOException, ParseException {
        Map<Integer, Game> map = new HashMap<>();
        if (path == null || !Files.exists(path)) {
            System.out.println("DEBUG readGamesAsMap: fisierul nu exista");
            return map;
        }
        System.out.println("DEBUG readGamesAsMap: citesc din " + path.toAbsolutePath());
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            System.out.println("DEBUG readGamesAsMap: root parsed, type=" + (root != null ? root.getClass().getSimpleName() : "null"));
            JSONArray arr = asArray(root);

            if (arr == null) {
                System.out.println("DEBUG readGamesAsMap: arr is null");
                return map;
            }
            System.out.println("DEBUG readGamesAsMap: arr.size=" + arr.size());

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) {
                    System.out.println("DEBUG readGamesAsMap: item is not JSONObject");
                    continue;
                }

                int id = asInt(obj.get("id"), -1);
                if (id < 0) {
                    continue;
                }
                Game g = new Game(id);

                JSONArray playersArr = asArray(obj.get("players"));
                if (playersArr != null && playersArr.size() >= 2) {
                    JSONObject player1 = asObject(playersArr.get(0));
                    JSONObject player2 = asObject(playersArr.get(1));
                    List<Player> players = new ArrayList<>();
                    String player1Email = asString(player1.get("email"));
                    String player2Email = asString(player2.get("email"));
                    String player1Color = asString(player1.get("color"));
                    String player2Color = asString(player2.get("color"));
                    String currentPlayerColor = asString(obj.get("currentPlayerColor"));
                    Player mainPlayer = new Player();
                    mainPlayer.setName(player1Email);
                    mainPlayer.setColor(player1Color);
                    mainPlayer.setPoints(asInt(player1.get("points"), 0));
                    Player opponent = new Player();
                    opponent.setName(player2Email);
                    opponent.setColor(player2Color);
                    opponent.setPoints(asInt(player2.get("points"), 0));
                    g.setPlayer(mainPlayer);
                    g.setOpponent(opponent);
                    if (currentPlayerColor.equals(player1Color)) {
                        g.setAttackOrder(0);
                    } else {
                        g.setAttackOrder(1);
                    }
                    // if (currentPlayerColor.equals(player1Color)) {
                    //     g.setPlayer(mainPlayer);
                    //     g.setOpponent(opponent);
                    //     g.setAttackOrder(0);
                    // } else {
                    //     g.setPlayer(opponent);
                    //     g.setOpponent(mainPlayer);
                    //     g.setAttackOrder(1);
                    // }
                }

                // board array
                JSONArray boardArr = asArray(obj.get("board"));
                if (boardArr != null) {
                    List<Map<String, String>> boardInfo = new ArrayList<>();
                    for (Object bItem : boardArr) {
                        JSONObject bObj = asObject(bItem);
                        if (bObj == null) {
                            continue;
                        }

                        String type = asString(bObj.get("type"));
                        String color = asString(bObj.get("color"));
                        String position = asString(bObj.get("position"));
                        Map<String, String> pieceInfo = new HashMap<>();
                        pieceInfo.put("type", type);
                        pieceInfo.put("color", color);
                        pieceInfo.put("position", position);
                        boardInfo.add(pieceInfo);
                    }
                    Board board = new Board();
                    board.initializeJSONvers(boardInfo);
                    g.setBoard(board);
                    board.setColorView(g.getPlayer().getPiecesColor());
                    board.refreshPieces();
                    g.getPlayer().refreshPlayesPieces(board);
                    g.getOpponent().refreshPlayesPieces(board);
                }

                // Parse optional moves array
                JSONArray movesArr = asArray(obj.get("moves"));
                if (movesArr != null) {
                    List<Move> moves = new ArrayList<>();
                    for (Object mItem : movesArr) {
                        JSONObject mObj = asObject(mItem);
                        if (mObj == null) {
                            continue;
                        }

                        String playerColorRaw = asString(mObj.get("playerColor"));
                        String fromRaw = asString(mObj.get("from"));
                        String toRaw = asString(mObj.get("to"));
                        Enums.Colors playerColor = Enums.Colors.valueOf(playerColorRaw.toUpperCase());
                        Position from = toPosition(fromRaw);
                        Position to = toPosition(toRaw);
                        Move move = new Move(playerColor, from, to, null);
                        moves.add(move);
                    }
                    g.setMoves(moves);
                }
                map.put(id, g);
                parseOldCapturedPieces(g);
            }
        }
        return map;
    }

    // -------- helper converters --------
    private static void parseOldCapturedPieces(Game game) {
        Map<String, Integer> initialCount = countPieces(createInitialBoard());
        Map<String, Integer> currentCount = countPieces(game.getBoard());

        for (Map.Entry<String, Integer> entry : initialCount.entrySet()) {
            String key = entry.getKey();
            int missing = entry.getValue() - currentCount.getOrDefault(key, 0);

            for (int i = 0; i < missing; i++) {
                Piece capturedPiece = createPieceFromKey(key);
                addCapturedPiece(game, capturedPiece);
            }
        }
    }

    private static Board createInitialBoard() {
        Board board = new Board();
        board.initialize();
        return board;
    }

    private static Map<String, Integer> countPieces(Board board) {
        Map<String, Integer> count = new HashMap<>();
        for (ChessPair<Position, Piece> pair : board.getBoardPieces()) {
            Piece piece = pair.getValue();
            String key = piece.type() + "_" + piece.getColor();
            count.put(key, count.getOrDefault(key, 0) + 1);
        }
        return count;
    }

    private static Piece createPieceFromKey(String key) {
        char type = key.charAt(0);
        Enums.Colors color = key.contains("WHITE") ? Enums.Colors.WHITE : Enums.Colors.BLACK;
        return PieceFactoryPattern.charToPiece(type, color, new Position('A', 1));
    }

    private static void addCapturedPiece(Game game, Piece capturedPiece) {
        boolean sameColorAsPlayer = capturedPiece.getColor() == game.getPlayer().getPiecesColor();
        Player capturingPlayer = sameColorAsPlayer ? game.getOpponent() : game.getPlayer();
        capturingPlayer.getCapturedPieces().add(capturedPiece);
    }

    public static Position toPosition(String raw) {
        char x = raw.charAt(0);
        int y = Character.getNumericValue(raw.charAt(1));
        return new Position(x,y);
    }
    private static JSONArray asArray(Object o) {
        return (o instanceof JSONArray) ? (JSONArray) o : null;
    }

    private static JSONObject asObject(Object o) {
        return (o instanceof JSONObject) ? (JSONObject) o : null;
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static int asInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return o != null ? Integer.parseInt(String.valueOf(o)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static long asLong(Object o, long def) {
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return o != null ? Long.parseLong(String.valueOf(o)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
