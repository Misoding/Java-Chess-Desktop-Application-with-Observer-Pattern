package GUI_Components;

import GUI_Components.CustomGeneral_Components.*;
import errors.InvalidMoveException;
import game_logic.*;
import main_package.Main;
import misc.Enums;
import pieces_details.Piece;
import user_details.Player;
import user_details.User;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import static GUI_Components.CustomGeneral_Components.Global_Style.*;
import static GUI_Components.CustomGeneral_Components.Global_Style.TEXT_BOLD_MEDIUM;
import static GUI_Components.CustomGeneral_Components.Global_Style.TEXT_BOLD_SMALL;
import static GUI_Components.CustomGeneral_Components.Global_Style.TEXT_COLOR;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class GameFrame extends JFrame {
    private Main main;
    private Game game;
    private String username;
    private JLabel userImage;
    private JLabel opponentImage;
    private JLabel turnImageLabel;
    private JLabel turnTextLabel;
    private ChessButton[][] chessButons;
    private String userCaptures;
    private String opponentCaptures;
    private JPanel userCapturesPanel;
    private JPanel opponentCapturesPanel;
    private JPanel movesPanel;
    private Position clickedPos;
    private List<Position> positionsToColor;
    private CustomLabel userPointsLabel;

    private ClickButton resignButton;
    private ClickButton saveExitButton;
    private ClickButton mainMenuButton;
    public GameFrame(Main main, Game game) {
        this.main = main;
        this.game = game;
        this.username = main.getCurrentUser().getEmail();

        this.userImage = IconBuilder.iconCreate("/images/userTurn.png", 32, 32);
        this.opponentImage = IconBuilder.iconCreate("/images/robotTurn.png", 32, 32);

        this.turnImageLabel = new JLabel();
        this.turnTextLabel = new CustomLabel("", TEXT_COLOR, TEXT_BOLD_SMALL);
        this.generateTableButtons();
        this.userCaptures = "";
        this.opponentCaptures = "";
        this.drawEntireFrame();
        this.startGameInUi();
        this.clickedPos = null;
        this.positionsToColor = new ArrayList<>();
        if (game.computerTurn()) {
            Timer computerTime = new Timer(1000, e-> {
                this.processComputer();
            });
            computerTime.setRepeats(false);
            computerTime.start();
        }
        this.modifyHistory();
        this.updatePoints();
//        this.buildFinalWindow();
        this.resignButton.addActionListener(e -> processResign());
        this.saveExitButton.addActionListener(e -> processSaveExit());
        this.mainMenuButton.addActionListener(e -> processMainMenu());
    }
    private void processResign() {
        Player player = game.getPlayer();
        Player opponent = game.getOpponent();
        player.leaveLosePoints();
        opponent.leaveWinPoints();
        this.main.getCurrentUser().setPoints(player.getPoints());
        try {
            this.main.getCurrentUser().removeGame(this.game);
            this.main.getExistingGames().remove(game.getGameId());
        } catch (Exception exception) {
            System.out.println("Error: " + exception);
        }
        UserNotifyHelper.displayInfo(this, "You resigned, back to main menu", "User resign");
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame menu = new MainMenuFrame(main);
            menu.setVisible(true);
        });
    }

    private void processSaveExit() {
        UserNotifyHelper.displayInfo(this, "Game saved, process exit", "Save and exit");
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            main.setCurrentUser(null);
            new LoginAndRegisterWindow(main);
        });
    }
    private void processMainMenu() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame menu = new MainMenuFrame(main);
            menu.setVisible(true);
        });
    }
    private ChessPair<Integer, Integer> transformCoords(Position position, Enums.Colors color) {
        int i = 8 - position.getY();
        int j = position.getX() - 'A';
        if (color == Enums.Colors.BLACK) {
            i = 7 - i;
            j = 7 - j;
        }
        return new ChessPair<>(i, j);
    }
    private Position transformToPos(int i, int j) {
        if (this.game.getPlayer().getPiecesColor() == Enums.Colors.BLACK) {
            i = 7 - i;
            j = 7 - j;
        }
        char x = (char) (j + 'A');
        int y = 8 - i;
        return new Position(x,y);
    }
    private void startGameInUi() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.chessButons[i][j].setText("");
                this.chessButons[i][j].paintBack();
                this.chessButons[i][j].setFont(TEXT_PIECES);
            }
        }
        TreeSet<ChessPair<Position, Piece>> chessPiecesObjects = game.getBoard().getBoardPieces();
        for (ChessPair<Position, Piece> chessObject : chessPiecesObjects) {
            Enums.Colors userColor = game.getPlayer().getPiecesColor();
            ChessPair<Integer, Integer> coords = this.transformCoords(chessObject.getKey(), userColor);
            int i = coords.getKey();
            int j = coords.getValue();
            char symbol = pieceToChar(chessObject.getValue());
            this.chessButons[i][j].setText(""+symbol);
            if (chessObject.getValue().getColor() == Enums.Colors.WHITE) {
                chessButons[i][j].setForeground(TEXT_COLOR);
            } else {
                chessButons[i][j].setForeground(BACKGROUND_DARK);
            }

        }
        this.checkKingWarrning();
    }
    private void checkKingWarrning() {
        Enums.Colors userColor = game.getPlayer().getPiecesColor();
        boolean userInCheck = game.getBoard().checkForCheck(userColor);
        if (userInCheck) {
            Position king = game.getBoard().getKing(userColor);
            ChessPair<Integer, Integer> coords = this.transformCoords(king, userColor);
            int i = coords.getKey();
            int j = coords.getValue();
            this.chessButons[i][j].specialPaint(BOARD_RED_COLOR);
        }
    }
    public void refreshBoard() {
        this.startGameInUi();
    }
    public void selectPieceAt(Position pos) {
        Piece piece = game.getBoard().getPieceAt(pos);
        if (piece == null || piece.getColor() != this.game.getPlayer().getPiecesColor()) {
            return;
        }
        this.clickedPos = pos;
        for (Position posToCheck : piece.getPossibleMoves(this.game.getBoard())) {
            if (game.getBoard().isValidMove(pos, posToCheck)) {
                this.positionsToColor.add(posToCheck);
            }
        }
        this.startGameInUi();
        this.colorPossibleMoves();
    }
    private void colorPossibleMoves() {
        for (Position position : positionsToColor) {
            ChessPair<Integer, Integer> coords = this.transformCoords(position, game.getPlayer().getPiecesColor());
            int i = coords.getKey();
            int j = coords.getValue();
            this.chessButons[i][j].specialPaint(BOARD_GREEN_COLOR);
        }
        ChessPair<Integer, Integer> coords = this.transformCoords(this.clickedPos, game.getPlayer().getPiecesColor());
        int i = coords.getKey();
        int j = coords.getValue();
        this.chessButons[i][j].specialPaint(BOARD_SELECTED_COLOR);
    }

    private char pieceToChar(Piece piece) {
        boolean whiteStatus = piece.getColor() == Enums.Colors.WHITE ? true : false;
        char pieceChar = 'X';
        switch (piece.type()) {
            case 'K': {
                  return BLACK_KING;
            }
            case 'Q': {

                return BLACK_QUEEN;
            }
            case 'R': {
                return BLACK_ROOK;
            }
            case 'B': {
                return BLACK_BISHOP;
            }
            case 'N': {
                return BLACK_KNIGHT;
            }
            case 'P': {
                return BLACK_PAWN;
            }
        }
        return pieceChar;
    }
    private void generateTableButtons() {
        this.chessButons = new ChessButton[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessButton button = new ChessButton(i,j);
                this.chessButons[i][j] = button;
                this.chessButons[i][j].setText("");
                this.chessButons[i][j].addActionListener(e -> this.processTableClick(button));
                this.chessButons[i][j].addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            processDeselect();
                        }
                    }
                });
            }
        }
    }
    private void processDeselect() {
        if (this.clickedPos == null) {
            return;
        }
        this.clickedPos = null;
        this.positionsToColor = new ArrayList<>();
        this.startGameInUi();
    }
    private void processTableClick(ChessButton clickedButton) {
        boolean isUserTurn = game.PlayerTurn();
        if (!isUserTurn) {
            UserNotifyHelper.displayWarrning(this, "Heey bro, wait for your turn", "Not your turn");
            return;
        }
        int i = clickedButton.geti();
        int j = clickedButton.getj();
        Position originalPos = transformToPos(i,j);
        if (this.clickedPos == null) {
            Piece piece = game.getBoard().getPieceAt(originalPos);
            if (piece == null) {
                UserNotifyHelper.displayInfo(this, "You cannot select empty fields",
                        "Select your pieces");
                return;
            }
            if (piece.getColor() != this.game.getPlayer().getPiecesColor()) {
                UserNotifyHelper.displayWarrning(this, "You should select your pieces, not enemy ones",
                        "Wrong table select");
                return;
            }
            this.clickedPos = originalPos;
            for (Position posToCheck : piece.getPossibleMoves(this.game.getBoard())){
                if (game.getBoard().isValidMove(originalPos, posToCheck)) {
                    this.positionsToColor.add(posToCheck);
                }
            }
            this.startGameInUi();
            this.colorPossibleMoves();
        } else {
            if (originalPos.equals(this.clickedPos)) {
                this.clickedPos = null;
                this.positionsToColor = new ArrayList<>();
                this.startGameInUi();
                return;
            }

            if (!this.positionsToColor.contains(originalPos)) {
                Piece piece = game.getBoard().getPieceAt(originalPos);
                if (piece != null && piece.getColor() == this.game.getPlayer().getPiecesColor()) {
                    UserNotifyHelper.displayInfo(this, "You cannot move over your pieces", "Wrong move");
                    return;
                }
                UserNotifyHelper.displayWarrning(this, "You cannot move your piece here", "Wrong move");
                return;
            }
            try {
                game.turnManager(this.clickedPos, originalPos);
                if (game.getBoard().legitPromovation(originalPos)) {
                    this.processPiecePromote(originalPos, game.getPlayer().getPiecesColor());
                }
                this.clickedPos = null;
                this.positionsToColor = new ArrayList<>();
                this.startGameInUi();
                this.modifyTurnStatus();
                this.updateCapture();
                this.modifyHistory();
                this.updatePoints();

                if (!game.running()){
                    this.processFinishGame();
                    return;
                }
                if (game.computerTurn()) {
                    Timer timer = new Timer(1000, e -> {
                        this.processComputer();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            } catch(InvalidMoveException exception) {
                UserNotifyHelper.displayInfo(this, "You cannot move here", "Wrong move");
                this.clickedPos = null;
                this.positionsToColor = new ArrayList<>();
                this.startGameInUi();
            }
        }
    }
    private void processPiecePromote(Position pos, Enums.Colors color) {
        String[] chooseList = {"Bishop", "Knight", "Queen", "Rook"};

        int choice = JOptionPane.showOptionDialog(
                this, "You can promote to:", "Promotion", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, chooseList, chooseList[0]
        );
        Enums.PiecesTypes promType = Enums.PiecesTypes.QUEEN;
        switch (choice) {
            case 0:
                promType = Enums.PiecesTypes.BISHOP;
                break;
            case 1:
                promType = Enums.PiecesTypes.KNIGHT;
                break;
            case 2:
                promType = Enums.PiecesTypes.QUEEN;
                break;
            case 3:
                promType = Enums.PiecesTypes.ROOK;
                break;
        }
        this.game.getBoard().promotePiece(pos, promType, color);
    }
    private void processComputer() {
        Player computer = game.getOpponent();
        Board board = game.getBoard();
        HashSet<ChessPair<Position, Position>> moves = new HashSet<>();
        for (Piece piece : board.getPiecesColored(computer.getPiecesColor())){
            Position initiPosition = piece.getPosition();
            for (Position finalPosition : piece.getPossibleMoves(this.game.getBoard())) {
                if (board.isValidMove(initiPosition, finalPosition)) {
                    moves.add(new ChessPair(initiPosition, finalPosition));
                }
            }
        }
        if (moves.isEmpty()) {
            UserNotifyHelper.displayInfo(this, "Congratulations, computer has no moves to commit",
                    "Computer has no moves");
        }
        try {
            List<ChessPair<Position, Position>> newMoves = new ArrayList(moves);
            int moveIndex = (int) (Math.random() * newMoves.size());

            game.turnManager(newMoves.get(moveIndex).getKey(), newMoves.get(moveIndex).getValue());
            if (game.getBoard().legitPromovation(newMoves.get(moveIndex).getValue())) {
                game.getBoard().promotePiece(newMoves.get(moveIndex).getValue(), Enums.PiecesTypes.QUEEN,
                        game.getOpponent().getPiecesColor());
            }
            this.startGameInUi();
            this.modifyTurnStatus();
            this.updateCapture();
            this.modifyHistory();
            this.updatePoints();
            if (!game.running()){
                this.processFinishGame();
            }
        } catch (InvalidMoveException exception) {
            this.processComputer();
        }
    }
    private void updateCapture() {
        this.userCapturesPanel.removeAll();
        this.opponentCapturesPanel.removeAll();
        JPanel userPiecesPanel = new JPanel();
        userPiecesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        userPiecesPanel.setOpaque(false);

        for (Piece piece : this.game.getPlayer().getCapturedPieces()) {
            char symbol = pieceToChar(piece);
            JLabel pieceLabel = new JLabel(String.valueOf(symbol));
            pieceLabel.setFont(TEXT_PIECES_SMALL);
            if (piece.getColor() == Enums.Colors.WHITE) {
                pieceLabel.setForeground(TEXT_COLOR);
            } else {
                pieceLabel.setForeground(BLACK_PIECES_COLOR);
            }
            userPiecesPanel.add(pieceLabel);
        }
        JPanel opponentPiecesPanel = new JPanel();
        opponentPiecesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        opponentPiecesPanel.setOpaque(false);

        for (Piece piece : this.game.getOpponent().getCapturedPieces()) {
            char symbol = pieceToChar(piece);
            JLabel pieceLabel = new JLabel(String.valueOf(symbol));
            pieceLabel.setFont(TEXT_PIECES_SMALL);

            if (piece.getColor() == Enums.Colors.WHITE) {
                pieceLabel.setForeground(TEXT_COLOR);
            } else {
                pieceLabel.setForeground(TEXT_COLOR);
            }

            opponentPiecesPanel.add(pieceLabel);
        }
        userCapturesPanel.add(userPiecesPanel);
        opponentCapturesPanel.add(opponentPiecesPanel);

        userCapturesPanel.revalidate();
        userCapturesPanel.repaint();
        opponentCapturesPanel.revalidate();
        opponentCapturesPanel.repaint();
    }
    private void buildFinalWindow() {
        JDialog finalWindow = new JDialog(this, "Final stats", true);
        finalWindow.setSize(500, 400);
        finalWindow.setLocationRelativeTo(this);

        JPanel finalPanel = new JPanel();
        finalPanel.setLayout(new BorderLayout());
        finalPanel.setBackground(BACKGROUND_LIGHT);
        finalPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel upperPanel = new JPanel();
        upperPanel.setOpaque(false);
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        CustomLabel finalDialogue = new CustomLabel("Game statistics", BUTTON_COLOR_VIOLET, TEXT_BOLD_BIG);

        // win stat
        Player winPlayer = game.getWinPlayer();
        String winText = "> ";
        Color winColor = BOARD_RED_COLOR;

        if (winPlayer != null) {
            if (winPlayer == game.getPlayer()) {
                winText += "YOU WON :)";
                winColor = BOARD_GREEN_COLOR;
            } else {
                winText += "YOU LOST :(";
            }
        }
        CustomLabel winLabel = new CustomLabel(winText, winColor, TEXT_BOLD_BIG);
        winLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        upperPanel.add(winLabel);
        upperPanel.add(Box.createVerticalStrut(20));

        finalDialogue.setAlignmentX(Component.LEFT_ALIGNMENT);
        CustomLabel playerPanelLabel = new CustomLabel("Players", BACKGROUND_MEDIUM, TEXT_BOLD_SMALL);
        playerPanelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        upperPanel.add(finalDialogue);

        JPanel playerPanel = new JPanel();
        playerPanel.setOpaque(false);
        playerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        playerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        playerPanel.setLayout(new BorderLayout());
        String userName = this.game.getPlayer().getName();
        String userColor = this.game.getPlayer().getPiecesColor() == Enums.Colors.WHITE ? "WHITE" : "BLACK";
        int userPoitns = this.game.getPlayer().getPoints();
        CustomLabel playerName = new CustomLabel(userName + " ["+userColor+"]", TEXT_COLOR, TEXT_NORMAL_SMALL);
        playerName.setForeground(TEXT_COLOR);
        CustomLabel totalPoints = new CustomLabel("Points: "+userPoitns, TEXT_COLOR, TEXT_BOLD_MEDIUM);
        totalPoints.setForeground(BOARD_GREEN_COLOR);

        playerPanel.add(playerName, BorderLayout.WEST);
        playerPanel.add(totalPoints, BorderLayout.EAST);

        JPanel opponentPanel = new JPanel();
        opponentPanel.setOpaque(false);
        opponentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        opponentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        opponentPanel.setLayout(new BorderLayout());
        String opponentName = this.game.getOpponent().getName();
        String opponentColor = this.game.getOpponent().getPiecesColor() == Enums.Colors.WHITE ? "WHITE" : "BLACK";
        int opponentPoints = this.game.getOpponent().getPoints();
        CustomLabel opponentNameLabel = new CustomLabel(opponentName + " ["+opponentColor+"]", TEXT_COLOR, TEXT_NORMAL_SMALL);
        playerName.setForeground(TEXT_COLOR);
        CustomLabel opponentPointsLabel = new CustomLabel("Points: "+opponentPoints, TEXT_COLOR, TEXT_BOLD_MEDIUM);
        opponentPointsLabel.setForeground(BOARD_GREEN_COLOR);

        opponentPanel.add(opponentNameLabel, BorderLayout.WEST);
        opponentPanel.add(opponentPointsLabel, BorderLayout.EAST);

        upperPanel.add(playerPanel);
        upperPanel.add(opponentPanel);

        int movesNum = this.game.getGameMoves().size();
        CustomLabel moveHistoryLabel = new CustomLabel("Move History ["+movesNum+"]", BUTTON_COLOR_VIOLET_HOVER, TEXT_BOLD_SMALL);
        upperPanel.add(moveHistoryLabel);

        JPanel finalHistoryPanel = new JPanel();
        finalHistoryPanel.setOpaque(false);
        finalHistoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        finalHistoryPanel.setLayout(new BoxLayout(finalHistoryPanel, BoxLayout.Y_AXIS));
        finalHistoryPanel.setOpaque(false);
        finalHistoryPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        for (Move move : this.game.getGameMoves()) {
            String startStr = move.getUserColor() == Enums.Colors.WHITE ? "○" : "●";
            String labelText = startStr + " " + move.getFrom().toString() + "->" + move.getTo().toString();
            if (move.getCapturedPiece() != null ) {
                labelText += "[ capture : " + move.getCapturedPiece().type() + ")";
            }
            CustomLabel moveLabel = new CustomLabel(labelText, TEXT_COLOR, TEXT_NORMAL_SMALL);
            moveLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            finalHistoryPanel.add(moveLabel);
            finalHistoryPanel.add(Box.createVerticalStrut(3));
        }
        JScrollPane finalHistoryScroll = new JScrollPane(finalHistoryPanel);
        finalHistoryScroll.setOpaque(false);
        finalHistoryScroll.getViewport().setOpaque(false);
        finalHistoryScroll.setBorder(BorderFactory.createLineBorder(BACKGROUND_MEDIUM, 1));
        finalHistoryScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        finalHistoryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        finalHistoryScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        upperPanel.add(finalHistoryScroll);
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setBackground(BACKGROUND_MEDIUM);
        closeButton.setForeground(TEXT_COLOR);
        closeButton.setFocusPainted(false);
        closeButton.setFont(TEXT_NORMAL_SMALL);
        closeButton.addActionListener(e -> finalWindow.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        upperPanel.add(buttonPanel);

        finalPanel.add(upperPanel,  BorderLayout.CENTER);
        finalWindow.add(finalPanel);
        finalWindow.add(finalPanel);
        finalWindow.setVisible(true);

    }
    private void processFinishGame() {
        try {
            main.getCurrentUser().removeGame(game);
        } catch(Exception exception) {
            System.err.println(exception);
        }
        this.buildFinalWindow();
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            MainMenuFrame menu = new MainMenuFrame(main);
            menu.setVisible(true);
        });
    }
    private void modifyTurnStatus() {
        if (this.game == null) {
            return;
        }
        boolean isUserTurn = game.PlayerTurn();
        if (isUserTurn) {
            turnImageLabel.setIcon(userImage.getIcon());
            turnTextLabel.setText(" YOUR TURN");
        } else {
            turnImageLabel.setIcon(opponentImage.getIcon());
            turnTextLabel.setText(" OPPONENT TURN");
        }
        turnImageLabel.repaint();
        turnTextLabel.repaint();
    }
    private JPanel createTurnTrackerPanel() {
        JPanel turnTrackerPanel = new JPanel();
        turnTrackerPanel.setOpaque(false);
        turnTrackerPanel.setLayout(new GridBagLayout());
        this.modifyTurnStatus();
        this.turnImageLabel.setBorder(BorderFactory.createEmptyBorder(0,15,0,15));
        turnTrackerPanel.add(this.turnImageLabel);
        turnTrackerPanel.add(this.turnTextLabel);

        return turnTrackerPanel;
    }
    private JPanel drawHeaderPart() {
        JPanel headerPart = new JPanel();
        headerPart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        headerPart.setMinimumSize(new Dimension(200, 70));
        headerPart.setLayout(new BorderLayout());
        headerPart.setBackground(BACKGROUND_MEDIUM);


        //entire right part
        JPanel headerLeftPart = new JPanel();
//        headerLeftPart.setBackground(BACKGROUND_MEDIUM);
        headerLeftPart.setOpaque(false);
        // titlepanel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, Y_AXIS));
        titlePanel.setOpaque(false);

        //gametitle
        CustomLabel gameTitle = new CustomLabel("CHESS WAR II", TEXT_COLOR, TEXT_BOLD_BIG);
        gameTitle.setAlignmentX(Component.LEFT_ALIGNMENT);


        //gameSubtitle
        CustomLabel gameSubtitle = new CustomLabel("Final battle", TEXT_COLOR, TEXT_BOLD_SMALL);
        gameSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(gameTitle);
        titlePanel.add(gameSubtitle);

        //game icon
        ImageIcon gameIconRaw = new ImageIcon(getClass().getResource("/images/game_logo.png"));
        JLabel gameIcon = new JLabel(gameIconRaw);
        gameIcon.setBorder(BorderFactory.createEmptyBorder(5,30,0,10));
        gameIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerLeftPart.add(gameIcon, BorderLayout.CENTER);
        headerLeftPart.add(titlePanel, BorderLayout.WEST);

        //header right part
        JPanel headerRightPart = new JPanel();
        headerRightPart.setLayout(new BoxLayout(headerRightPart, X_AXIS));
//        headerRightPart.setBackground(BACKGROUND_MEDIUM);
        headerRightPart.setOpaque(false);

        JPanel pointsPanel = new JPanel();
        pointsPanel.setLayout(new BoxLayout(pointsPanel, Y_AXIS));
        pointsPanel.setOpaque(false);
        pointsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 20));

        CustomLabel pointsTitleLabel = new CustomLabel("Points", TEXT_COLOR, TEXT_NORMAL_SMALL);
        pointsTitleLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        userPointsLabel = new CustomLabel(String.valueOf(game.getPlayer().getPoints()), BOARD_GREEN_COLOR, TEXT_BOLD_MEDIUM);
        userPointsLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        pointsPanel.add(pointsTitleLabel);
        pointsPanel.add(userPointsLabel);

        CustomLabel userName = new CustomLabel(this.username, TEXT_COLOR, TEXT_BOLD_MEDIUM);
        userName.setAlignmentX(Component.RIGHT_ALIGNMENT);

        ImageIcon userIconRaw = new ImageIcon(getClass().getResource("/images/user.jpg"));
        JLabel userIcon = new JLabel(userIconRaw);
        userIcon.setAlignmentX(Component.RIGHT_ALIGNMENT);
        userIcon.setBorder(BorderFactory.createEmptyBorder(5,10,0,30));

        headerRightPart.add(pointsPanel);
        headerRightPart.add(userName, BorderLayout.CENTER);
        headerRightPart.add(userIcon, BorderLayout.WEST);

        JPanel turnTrackerPanel = this.createTurnTrackerPanel();

        headerPart.add(headerLeftPart, BorderLayout.WEST);
        headerPart.add(turnTrackerPanel,  BorderLayout.CENTER);
        headerPart.add(headerRightPart, BorderLayout.EAST);

        return headerPart;
    }
    private void updatePoints() {
        if (this.userPointsLabel != null) {
            this.userPointsLabel.setText(String.valueOf(game.getPlayer().getPoints()));
        }
    }
    private JPanel getMoveHistoryPanel() {
        JPanel moveHistoryPanel = new JPanel();
        moveHistoryPanel.setLayout(new BorderLayout());
        moveHistoryPanel.setBackground(BACKGROUND_MEDIUM);
        moveHistoryPanel.setPreferredSize(new Dimension(250, 0));
        moveHistoryPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, X_AXIS));
        titlePanel.setOpaque(false);

        JLabel historyIcon = IconBuilder.iconCreate("/images/history.png", 25,25);
        CustomLabel historyTitle = new CustomLabel("History", TEXT_COLOR, TEXT_BOLD_MEDIUM);
        historyTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        historyIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(historyIcon);
        titlePanel.add(historyTitle);

        this.movesPanel = new JPanel();
        movesPanel.setLayout(new BoxLayout(movesPanel, Y_AXIS));
        movesPanel.setOpaque(false);
        JScrollPane movesScrollPanel = new JScrollPane(movesPanel);
        movesScrollPanel.setOpaque(false);
        movesScrollPanel.getViewport().setOpaque(false);
        movesScrollPanel.setBorder(null);
        movesScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        movesScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        moveHistoryPanel.add(titlePanel, BorderLayout.NORTH);
        moveHistoryPanel.add(movesScrollPanel, BorderLayout.CENTER);
        return moveHistoryPanel;
    }
    private void modifyHistory() {
        this.movesPanel.removeAll();
        int id = 1;
        for (Move move : this.game.getGameMoves()) {
            String startChar = (move.getUserColor() == Enums.Colors.WHITE) ? "○" : "●";
            String moveText = id + "]: "+startChar + " " + move.getFrom().toString() + " -> " + move.getTo().toString();
            CustomLabel moveLabel = new CustomLabel(moveText, TEXT_COLOR, TEXT_BOLD_SMALL);
            moveLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            movesPanel.add(moveLabel);
            id++;
        }
        movesPanel.revalidate();
        movesPanel.repaint();
    }
    private JPanel getCentralBoardPanel() {
        JPanel centralBoardPanel = new JPanel();
        centralBoardPanel.setLayout(new GridBagLayout());
        centralBoardPanel.setOpaque(false);

        JPanel boardPanel = new JPanel();
        boardPanel.setBorder(BorderFactory.createLineBorder(BOARD_BORDER_COLOR, 10));
        boardPanel.setPreferredSize(new Dimension(532, 532));
        boardPanel.setLayout(new BoxLayout(boardPanel, Y_AXIS));

        for (int i = 0; i < 8; i++) {
            JPanel boardRow = new JPanel();
            boardRow.setLayout(new BoxLayout(boardRow, X_AXIS));
            boardRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            boardRow.setPreferredSize(new Dimension(512, 64));
            boardRow.setMaximumSize(new Dimension(512, 64));
            for (int j = 0; j < 8; j++) {
                ChessButton currentButton = this.chessButons[i][j];
                currentButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                currentButton.setPreferredSize(new Dimension(64, 64));
                currentButton.setMaximumSize(new Dimension(64, 64));
                boardRow.add(currentButton);
            }
            boardPanel.add(boardRow);
        }

        centralBoardPanel.add(boardPanel);
        return centralBoardPanel;
    }
    private JPanel getRightCommandPanel() {
        JPanel rightCommandPanel = new JPanel();
        rightCommandPanel.setLayout(new BorderLayout());
        rightCommandPanel.setOpaque(false);

        JPanel capturePiecesPanel = new JPanel();
        capturePiecesPanel.setLayout(new BoxLayout(capturePiecesPanel, Y_AXIS));
        capturePiecesPanel.setBackground(BACKGROUND_MEDIUM);
        capturePiecesPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        CustomLabel captureLabel = new CustomLabel("Captured Pieces", TEXT_COLOR, TEXT_BOLD_MEDIUM);
        captureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        capturePiecesPanel.add(captureLabel);

        // user captures
        CustomLabel userCaptureLabel = new CustomLabel("Your captures: ", TEXT_COLOR, TEXT_NORMAL_SMALL);
        userCaptureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userCaptureLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        capturePiecesPanel.add(userCaptureLabel);

        JPanel userCaptureBlock = new JPanel();
        userCaptureBlock.setLayout(new FlowLayout());

        this.userCapturesPanel =  userCaptureBlock;
        userCaptureBlock.setBackground(BACKGROUND_DARK);
        userCaptureBlock.setPreferredSize(new Dimension(200, 100));

        JScrollPane userCaptureScroll = new JScrollPane(userCaptureBlock);
        userCaptureScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userCaptureScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        userCaptureScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        userCaptureScroll.setBorder(null);
        userCaptureScroll.setMaximumSize(new Dimension(200, 100));
        capturePiecesPanel.add(userCaptureScroll);
        capturePiecesPanel.add(Box.createVerticalStrut(50));

        // opponent
        CustomLabel opponentCaptureLabel = new CustomLabel("Opponent captures: ", TEXT_COLOR, TEXT_NORMAL_SMALL);
        opponentCaptureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        opponentCaptureLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        capturePiecesPanel.add(opponentCaptureLabel);

        JPanel opponentCaptureBlock = new JPanel();
        opponentCaptureBlock.setLayout(new FlowLayout());
        this.opponentCapturesPanel =  opponentCaptureBlock;
        opponentCaptureBlock.setBackground(BACKGROUND_DARK);
        opponentCaptureBlock.setPreferredSize(new Dimension(200, 100));

        JScrollPane opponentCaptureScroll = new JScrollPane(opponentCaptureBlock);
        opponentCaptureScroll.setBorder(null);
        opponentCaptureScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        opponentCaptureScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        opponentCaptureScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        opponentCaptureScroll.setMaximumSize(new Dimension(200, 100));
        capturePiecesPanel.add(opponentCaptureScroll);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

         this.resignButton = new ClickButton("Resign", BUTTON_COLOR_RED, BACKGROUND_DARK);
        resignButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resignButton.setMaximumSize(new Dimension(200, 40));
        resignButton.setPreferredSize(new Dimension(200, 40));

        this.saveExitButton = new ClickButton("Save & Exit", BUTTON_COLOR_ORANGE, BACKGROUND_DARK);
        saveExitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveExitButton.setMaximumSize(new Dimension(200, 40));
        saveExitButton.setPreferredSize(new Dimension(200, 40));

        this.mainMenuButton = new ClickButton("Main menu", BUTTON_COLOR_VIOLET, BACKGROUND_DARK);
        mainMenuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainMenuButton.setMaximumSize(new Dimension(200, 40));
        mainMenuButton.setPreferredSize(new Dimension(200, 40));

        buttonsPanel.add(resignButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(saveExitButton);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(mainMenuButton);
        buttonsPanel.add(Box.createVerticalStrut(10));

        rightCommandPanel.add(capturePiecesPanel,  BorderLayout.NORTH);
        rightCommandPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return rightCommandPanel;
    }
    private JPanel getContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JPanel leftSide = getMoveHistoryPanel();
        JPanel centralSide = getCentralBoardPanel();
        JPanel rightSize = getRightCommandPanel();
        contentPanel.add(leftSide, BorderLayout.WEST);
        contentPanel.add(centralSide, BorderLayout.CENTER);
        contentPanel.add(rightSize, BorderLayout.EAST);
        return contentPanel;
    }
    private void drawEntireFrame() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, Y_AXIS));
        mainPanel.setMinimumSize(new Dimension(1300, 700));
        mainPanel.setPreferredSize(new Dimension(1300, 700));
        this.setSize(1300, 700);
        mainPanel.setBackground(BACKGROUND_LIGHT);
        JPanel headerPart = this.drawHeaderPart();
        mainPanel.add(headerPart);

        JPanel contentPanel = this.getContentPanel();
        mainPanel.add(contentPanel);
        this.add(mainPanel);
        this.pack();
        this.setVisible(true);

    }
}
