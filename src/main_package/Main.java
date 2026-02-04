package main_package;

import GUI_Components.LoadingComponent;
import errors.InvalidMoveException;
import game_logic.Game;
import game_logic.Position;
import game_logic.game_observers.HistoryObserver;
import game_logic.game_observers.JsonLogObserver;
import game_logic.game_observers.PointsObserver;
import misc.Enums;
import misc.JsonWriterUtil;
import pieces_details.Piece;
import user_details.Player;
import user_details.User;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private List<User> users;
    private Map<Integer, Game> existingGames;
    private User currentUser;
    private static final String ACCOUNTS_PATH = "src/input/accounts.json";
    private static final String GAMES_PATH = "src/input/games.json";
    private Scanner scanner;
    private static Main uniqueInstance = null;
    private Main() {
        this.users = new ArrayList<>();
        this.existingGames = new HashMap<>();
        this.currentUser = null;
        this.scanner = new Scanner(System.in);
    }
    public User dbEmailUserSearch(String email) {
        for (User user: users) {
            if (user.getEmail().toLowerCase().equals(email.toLowerCase())){
                return user;
            }
        }
        return null;
    }
    public static Main getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Main();
        }
        return uniqueInstance;
    }

    public void read() {
        try {
            Path accountsPath = Paths.get(ACCOUNTS_PATH);
            Path gamesPath = Paths.get(GAMES_PATH);

            if (!java.nio.file.Files.exists(accountsPath)) {
                System.err.println("Fisierul accounts.json nu exista la: " + accountsPath.toAbsolutePath());
                return;
            }
            if (!java.nio.file.Files.exists(gamesPath)) {
                System.err.println("Fisierul games.json nu exista la: " + gamesPath.toAbsolutePath());
                return;
            }

            this.users = JsonReaderUtil.readAccounts(accountsPath);
            this.existingGames = JsonReaderUtil.readGamesAsMap(gamesPath);
            syncGamesToUsers();
            System.out.println("Date incarcate: " + users.size() + " utilizatori, " + existingGames.size() + " jocuri");
        } catch (IOException e) {
            System.err.println("Eroare citire fisiere: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Eroare parsare JSON: " + e.getMessage());
        }
    }

    public void write() {
        try {
            JsonWriterUtil.write(Paths.get(ACCOUNTS_PATH), Paths.get(GAMES_PATH), users, existingGames);
            System.out.println("Date salvate cu succes");
        } catch (IOException e) {
            System.err.println("Eroare la salvare: " + e.getMessage());
        }
    }

    public User login(String email, String password) {
        if (email == null || password == null) {
            System.out.println("Email sau parola lipsa");
            return null;
        }
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                if (user.verifyPasspord(password)) {
                    this.currentUser = user;
                    System.out.println("Autentificare reusita: " + email + " (Puncte: " + user.getPoints() + ")");
                    return user;
                }
                System.out.println("Parola incorecta pentru " + email);
                return null;
            }
        }
        System.out.println("Utilizatorul " + email + " nu exista");
        return null;
    }

    public User newAccount(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                System.out.println("Email-ul " + email + " este deja folosit");
                return null;
            }
        }
        User newUser = new User(email, password);
        if (newUser.getEmail() == null) {
            System.out.println("Email invalid");
            return null;
        }
        if (newUser.getPassword() == null) {
            System.out.println("Parola trebuie sa aiba minim 6 caractere");
            return null;
        }
        users.add(newUser);
        this.currentUser = newUser;
        System.out.println("Cont creat cu succes: " + email);
        return newUser;
    }

    public void run() {
        while (currentUser == null) {
            System.out.println("1.Login 2.Cont nou 3.Iesire");
            String c = scanner.nextLine().trim();
            if (c.equals("1")) {
                System.out.print("Email: "); String e = scanner.nextLine().trim();
                System.out.print("Parola: "); String p = scanner.nextLine().trim();
                login(e, p);
            } else if (c.equals("2")) {
                System.out.print("Email: "); String e = scanner.nextLine().trim();
                System.out.print("Parola: "); String p = scanner.nextLine().trim();
                newAccount(e, p);
            } else if (c.equals("3")) {
                System.exit(0);
            }
        }
        boolean running = true;
        while (running) {
            System.out.println(currentUser.getEmail() + " | Puncte:" + currentUser.getPoints());
            System.out.println("1.Joc nou 2.Continua 3.Jocuri 4.Logout");
            String c = scanner.nextLine().trim();
            if (c.equals("1")) startNewGame();
            else if (c.equals("2")) continueExistingGame();
            else if (c.equals("3")) displayUserGames();
            else if (c.equals("4")) { currentUser = null; running = false; }
        }
        write();
        scanner.close();
    }

    private void startNewGame() {
        int id = existingGames.isEmpty() ? 1 : Collections.max(existingGames.keySet()) + 1;
        Game game = new Game(id);
        Player player = new Player(currentUser.getEmail(), Enums.Colors.WHITE);
        Player computer = new Player("computer", Enums.Colors.BLACK);
        game.setPlayer(player);
        game.setOpponent(computer);
        game.setPlayerId(users.indexOf(currentUser));
        game.setOpponentId(-1);
        game.start();
        game.addObserver(new HistoryObserver());
        game.addObserver(new PointsObserver(game, currentUser));
        game.addObserver(new JsonLogObserver(game, currentUser, existingGames, users));
    
         currentUser.addGame(game);
        existingGames.put(id, game);
        playGame(game);
        // write();
    }

    private void continueExistingGame() {
        List<Integer> ids = currentUser.getActiveGamesIds();
        if (ids.isEmpty()) {
            System.out.println("Nu ai niciun joc activ");
            return;
        }
        System.out.println("Jocurile tale active:");
        for (int i = 0; i < ids.size(); i++) {
            Game g = existingGames.get(ids.get(i));
            if (g != null) System.out.println((i+1) + ". Joc #" + ids.get(i) + " - " + g.getGameMoves().size() + " mutari");
        }
        System.out.print("Alege numarul jocului: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < ids.size()) {
                Game g = existingGames.get(ids.get(idx));
                if (g != null) {
                    g.resume();
                    g.addObserver(new HistoryObserver());
                    g.addObserver(new PointsObserver(g, currentUser));
                    g.addObserver(new JsonLogObserver(g, currentUser, existingGames, users));
                
                    playGame(g);
                    // write();
                }
            } else {
                System.out.println("Numar invalid, trebuie intre 1 si " + ids.size());
            }
        } catch (NumberFormatException e) {
            System.out.println("Introdu un numar valid");
        }
    }

    private void playGame(Game game) {
        while (game.running()) {
            game.getBoard().display();
            if (game.computerTurn()) {
                Player comp = game.getOpponent();
                List<Piece> pieces = game.getBoard().getPiecesColored(comp.getPiecesColor());
                List<Position> froms = new ArrayList<>(), tos = new ArrayList<>();
                for (Piece p : pieces) {
                    Position from = p.getPosition();
                    for (Position to : p.getPossibleMoves(game.getBoard())) {
                        if (game.getBoard().isValidMove(from, to)) { froms.add(from); tos.add(to); }
                    }
                }
                if (!froms.isEmpty()) {
                    int r = (int)(Math.random() * froms.size());
                    try {
                        System.out.println("Computer muta: " + froms.get(r) + " -> " + tos.get(r));
                        game.turnManager(froms.get(r), tos.get(r));
                        if (game.checkForCheckMate()) break;
                    } catch (InvalidMoveException e) { game.switchPlayer(); }
                } else {
                    System.out.println("Computer nu are mutari valide - joc terminat");
                    break;
                }
                continue;
            }
            Player p = game.getPlayer();
            System.out.println("Tura ta: " + p.getName() + " (" + p.getPiecesColor() + ") | Puncte: " + p.getPoints());
            if (game.getBoard().checkForCheck(p.getPiecesColor())) System.out.println("ATENTIE: Esti in SAH!");
            System.out.println("Comenzi: [pozitie]=vezi mutari, [de-la]=muta, resign, exit");
            System.out.print("> ");
            String in = scanner.nextLine().trim();
            if (in.equalsIgnoreCase("exit")) {
                System.out.println("Joc salvat, poti continua mai tarziu");
                break;
            }
            if (in.equalsIgnoreCase("resign")) {
                p.leaveLosePoints();
                game.getOpponent().leaveWinPoints();
                currentUser.setPoints(currentUser.getPoints() + p.getPoints());
                System.out.println("Ai renuntat. Puncte pierdute: -150. Total: " + currentUser.getPoints());
                try { currentUser.removeGame(game); } catch (Exception e) {}
                break;
            }
            if (in.length() == 2 && !in.contains("-")) {
                try {
                    Position pos = parsePosition(in);
                    Piece piece = game.getBoard().getPieceAt(pos);
                    if (piece == null) {
                        System.out.println("Nu exista piesa la pozitia " + in);
                    } else if (piece.getColor() != p.getPiecesColor()) {
                        System.out.println("Piesa de la " + in + " nu iti apartine");
                    } else {
                        List<Position> moves = piece.getPossibleMoves(game.getBoard());
                        if (moves.isEmpty()) {
                            System.out.println("Piesa " + piece.type() + " nu are mutari posibile");
                        } else {
                            System.out.print("Mutari posibile pentru " + piece.type() + ": ");
                            for (Position m : moves) System.out.print(m + " ");
                            System.out.println();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Pozitie invalida: " + in + " (format: A1-H8)");
                }
                continue;
            }
            String[] parts = in.split("[-\\s]+");
            if (parts.length == 2) {
                try {
                    game.turnManager(parsePosition(parts[0]), parsePosition(parts[1]));
                    System.out.println("Mutare efectuata: " + parts[0] + " -> " + parts[1]);
                    if (game.checkForCheckMate()) {
                        System.out.println("CHECKMATE! Ai castigat! +300 puncte");
                        p.winCheckPoints();
                        currentUser.setPoints(currentUser.getPoints() + p.getPoints());
                        System.out.println("Total puncte: " + currentUser.getPoints());
                        try { currentUser.removeGame(game); } catch (Exception e) {}
                        break;
                    }
                } catch (InvalidMoveException e) {
                    System.out.println("Mutare invalida: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Eroare: pozitie invalida (format: E2-E4)");
                }
            } else {
                System.out.println("Comanda necunoscuta. Foloseste: E2 sau E2-E4 sau resign sau exit");
            }
        }
    }

    private void displayUserGames() {
        List<Integer> ids = currentUser.getActiveGamesIds();
        if (ids.isEmpty()) {
            System.out.println("Nu ai niciun joc activ");
            return;
        }
        System.out.println("Jocurile tale active (" + ids.size() + "):");
        for (int id : ids) {
            Game g = existingGames.get(id);
            if (g != null) {
                System.out.println("  Joc #" + id + ": " + g.getGameMoves().size() + " mutari, puncte: " + g.getPlayer().getPoints());
            }
        }
    }

    private void syncGamesToUsers() {
        for (User user : users) {
            for (Integer id : user.getActiveGamesIds()) {
                Game g = existingGames.get(id);
                if (g != null) user.getActiveGames().add(g);
            }
        }
    }

    private Position parsePosition(String s) {
        if (s == null || s.length() != 2) throw new IllegalArgumentException();
        char x = Character.toUpperCase(s.charAt(0));
        int y = Character.getNumericValue(s.charAt(1));
        if (x < 'A' || x > 'H' || y < 1 || y > 8) throw new IllegalArgumentException();
        return new Position(x, y);
    }

    public List<User> getUsers() { return this.users; }
    public Map<Integer, Game> getExistingGames() { return this.existingGames; }
    public User getCurrentUser() { return this.currentUser; }
    public boolean setUsers(List<User> users) { this.users = users; return true; }
    public boolean setGames(Map<Integer, Game> games) { this.existingGames = games; return true; }
    public boolean setCurrentUser(User user) { this.currentUser = user; return true; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Main mainApp = Main.getInstance();
        mainApp.read();
        SwingUtilities.invokeLater(() -> {
            new LoadingComponent(mainApp, 1);
        });
    }
}
