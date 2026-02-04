package game_logic;

import Interfaces.GameObserver;
import errors.InvalidMoveException;
import misc.Enums;
import pieces_details.Piece;
import user_details.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int gameId;
    private Board board;
    private Player player;
    private Player opponent;
    private List<Move> gameMoves;
    private int playerId;
    private int opponentId;
    private int attackOrder;
    private List<GameObserver> gameObservers;
    private Player winPlayer = null;
    public Game(int gameId) {
        this.gameId = gameId;
        this.board = new Board();
        this.gameMoves = new ArrayList<>();
        this.attackOrder = 0;
        this.gameObservers = new ArrayList<>();
    }
    public void addObserver(GameObserver gameObserver) {
        this.gameObservers.add(gameObserver);
    }
    public void removeObserver(GameObserver gameObserver) {
        this.gameObservers.remove(gameObserver);
    }
    private void triggerMoveObservers(Move move) {
        for (GameObserver gameObserver : gameObservers) {
            gameObserver.onMoveMade(move);
        }
    }
    private void triggerCaptureObservers(Piece piece) {
        for (GameObserver gameObserver : gameObservers) {
            gameObserver.onPieceCaptured(piece);
        }
    }
    private void triggerPlayerSwitch(Player player) {
        for (GameObserver gameObserver : gameObservers) {
            gameObserver.onPlayerSwitch(player);
        }
    }
    public int getId(){
        return  this.gameId;
    }
    public Board getBoard() {
        return this.board;
    }
    public Player getPlayer() {
        return this.player;
    }

    public Player getOpponent() {
        return opponent;
    }
    public List<Move> getGameMoves() {
        return this.gameMoves;
    }
    public int getPlayerId() {
        return this.playerId;
    }
    public int getOpponentId() {
        return this.opponentId;
    }
    public int getGameId() {
        return this.gameId;
    }
    public void setBoard(Board board) {
        this.board = board;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    public void setOpponentId(int opponentId) {
        this.opponentId = opponentId;
    }
    public void setAttackOrder(int attackOrder) {
        this.attackOrder = attackOrder;
    }
    public void setMoves(List<Move> moves){
        this.gameMoves = new ArrayList<>(moves);
    }
    public void start() {
        this.board.initialize();
        this.gameMoves.clear();
        if (this.player.getPiecesColor() == Enums.Colors.WHITE) {
            this.attackOrder = 0;
        } else {
            this.attackOrder = 1;
        }
        this.board.setColorView(player.getPiecesColor());
        this.opponent.refreshPlayesPieces(board);
        this.player.refreshPlayesPieces(board);
        System.out.println("\n=== JOC NOU PORNIT ===");
        System.out.println("Jucător: " + player.getName() + " (" + this.player.getPiecesColor() + ")");
        System.out.println("Adversar: " + opponent.getName() + " (" + this.opponent.getPiecesColor() + ")");
    }
    public void resume() {
        if (!this.gameMoves.isEmpty()) {
            Move lastMove = this.gameMoves.get(this.gameMoves.size() - 1);
            Enums.Colors lastPlayerColor = this.player.getPiecesColor();
            if (player.getPiecesColor() == lastPlayerColor) {
                this.attackOrder = 1;
            } else {
                this.attackOrder = 0;
            }
        }
        this.board.setColorView(player.getPiecesColor());
        this.opponent.refreshPlayesPieces(board);
        this.player.refreshPlayesPieces(board);
        board.refreshPieces();
        System.out.println("\n=== JOC RELUAT ===");
        System.out.println("Jucător: " + player.getName() + " (" + this.player.getPiecesColor() + ")");
        System.out.println("Adversar: " + opponent.getName() + " (" + this.opponent.getPiecesColor() + ")");
        System.out.println("Mutări efectuate: " + this.gameMoves.size());
    }
    public void switchPlayer() {
        this.attackOrder = (attackOrder == 0) ? 1 : 0;
        board.setColorView(this.getCurrentAttacker().getPiecesColor());
        System.out.println("\n\t Acum merge :" + this.getCurrentAttacker().getName());
        triggerPlayerSwitch(this.getCurrentAttacker());
    }
    public boolean checkForCheckMate() {
        Player player = getCurrentAttacker();
        Player opponent = getOpponentInGame();
        boolean gameOver = board.advancedCheckForCheck(player.getPiecesColor());
        if (gameOver) {
            System.out.println("\t---Game Over---\n" +
                    "[Castigator]: " + opponent.getName());
            opponent.winCheckPoints();
            player.looseCheckPoints();
            this.winPlayer = opponent;
            return true;
        }

        boolean opponentGameOver = board.advancedCheckForCheck(opponent.getPiecesColor());
        if (opponentGameOver) {
            System.out.println("\t---Game Over---\n" +
                    "[Castigator]: " + player.getName());
            player.winCheckPoints();
            opponent.looseCheckPoints();
            this.winPlayer = player;
            return true;
        }
        return false;
    }
    public Player getWinPlayer() {
        return this.winPlayer;
    }
    public void addMove(Player p, Position from, Position to) {
        Piece pieceToCapture = null;
        List<Piece> pieces = p.getCapturedPieces();
        if (!pieces.isEmpty()) {
            pieceToCapture = pieces.get(pieces.size() - 1);
        }
        Move move = new Move(p.getPiecesColor(), from, to, pieceToCapture);
        this.gameMoves.add(move);
        triggerMoveObservers(move);
        if (pieceToCapture != null) {
            triggerCaptureObservers(pieceToCapture);
        }
    
        Player opponent = getOpponentInGame();
        if (board.checkForCheck(opponent.getPiecesColor())) {
            System.out.println("SAH WARRNING" + opponent.getName());
        }
    }
    public Player getCurrentAttacker() {
        return (attackOrder == 0) ? this.player : this.opponent;
    }
    public Player getOpponentInGame() {
        return (opponentId == 0) ? this.opponent : this.player;
    }
    public boolean computerTurn() {
        return attackOrder == 1;
    }
    public boolean PlayerTurn() {
        return attackOrder == 0;
    }
    public void turnManager(Position from, Position to) throws InvalidMoveException {
        Player player = this.getCurrentAttacker();
        try {
            player.makeMove(from, to, board);
            this.addMove(player, from, to);
            this.board.display();
            if (this.checkForCheckMate()) {
                return;
            }
            this.switchPlayer();
        } catch (InvalidMoveException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
    public boolean running() {
        Position userKing = this.board.getKing(player.getPiecesColor());
        Position opponentKing = this.board.getKing(opponent.getPiecesColor());
        if (userKing == null || opponentKing == null) {
            return false;
        }
        return (!this.board.advancedCheckForCheck(player.getPiecesColor()) &&
                !this.board.advancedCheckForCheck(opponent.getPiecesColor()));
    }
    public void displayFinalScore() {
        System.out.println("[FinalScore]");
        System.out.println(player.getName() + " " + player.getPoints());
        System.out.println(opponent.getName() + " " + opponent.getPoints());
    }
    public void leavePenalties(Player p) {
        Player win = (p == this. player) ? this.player : this.opponent;
        System.out.println("[LeavePenalties]");
        win.leaveWinPoints();
        p.leaveLosePoints();
        displayFinalScore();
    }

}
