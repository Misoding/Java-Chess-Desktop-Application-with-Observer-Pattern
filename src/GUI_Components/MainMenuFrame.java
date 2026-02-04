package GUI_Components;

import GUI_Components.CustomGeneral_Components.CustomLabel;
import GUI_Components.CustomGeneral_Components.MainMenuClickButton;
import GUI_Components.CustomGeneral_Components.UserNotifyHelper;
import game_logic.Game;
import game_logic.game_observers.HistoryObserver;
import game_logic.game_observers.JsonLogObserver;
import game_logic.game_observers.PointsObserver;
import main_package.Main;
import misc.Enums;
import user_details.Player;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static GUI_Components.CustomGeneral_Components.Global_Style.*;

public class MainMenuFrame extends JFrame {
    private Main main;
    private String username;
    private int userPoints;
    private int activeGames;

    private MainMenuClickButton startButton;
    private MainMenuClickButton restartButton;
    private MainMenuClickButton leaveButton;

    public MainMenuFrame(Main main) {
        this.main = main;
        this.username = main.getCurrentUser().getEmail();
        this.userPoints = main.getCurrentUser().getPoints();
        this.activeGames = 0;
        Map<Integer, Game> totalGames = main.getExistingGames();
        for (Integer gameIdx : totalGames.keySet()) {
            Game checkGame = main.getExistingGames().get(gameIdx);
            if (checkGame != null && checkGame.getPlayer().getName().equals(main.getCurrentUser().getEmail())){
                this.activeGames++;
            }
        }
        this.drawMainMenuFrame();

        startButton.addActionListener(e -> processNewGame());
        restartButton.addActionListener(e -> processRestartGame());
        leaveButton.addActionListener(e -> processLeaveAccount());
    }
    private void processNewGame() {
        this.setVisible(false);
        int id = this.main.getExistingGames().isEmpty() ? 1 : Collections.max(main.getExistingGames().keySet())+1;
        Game game = new Game(id);
        boolean userColor = Math.random() < 0.5;
        Enums.Colors playerColor = userColor ? Enums.Colors.WHITE : Enums.Colors.BLACK;
        Enums.Colors opponentColor = userColor ? Enums.Colors.BLACK : Enums.Colors.WHITE;

        Player player = new Player(this.main.getCurrentUser().getEmail(),playerColor);
        Player opponent = new Player("Computer",opponentColor);
        game.setPlayer(player);
        game.setOpponent(opponent);
        game.setPlayerId(main.getUsers().indexOf(main.getCurrentUser()));
        game.setOpponentId(-1);
        game.start();
        game.addObserver(new HistoryObserver());
        game.addObserver(new PointsObserver(game, this.main.getCurrentUser()));
        game.addObserver(new JsonLogObserver(game, main.getCurrentUser(), main.getExistingGames(), main.getUsers()));
        main.getCurrentUser().addGame(game);
        main.getExistingGames().put(id, game);
        String userColorString = userColor ? "You are white" : "You are on the dark side";
        UserNotifyHelper.displayInfo(this, userColorString, "Your color is");
        SwingUtilities.invokeLater(() -> {
            GameFrame gameUi = new GameFrame(main, game);
        });
    }
    
    private void processRestartGame() {
        List<Integer> games = main.getCurrentUser().getActiveGamesIds();
        if (games.isEmpty()) {
            UserNotifyHelper.displayInfo(this, "You have no games to continue", "No games to continue");
            return;
        }
        String[] gamesToContinue = new String[games.size()];
        for (int i = 0; i < games.size(); i++) {
            Game existingGame = main.getExistingGames().get(games.get(i));
            if (existingGame != null) {
                gamesToContinue[i] = "Game [" + games.get(i) + "] : " +
                existingGame.getGameMoves().size() + " moves";
            }
        }
        String continuedGame = (String) JOptionPane.showInputDialog(this, "Choose one of those games", "Game continuing",
                JOptionPane.PLAIN_MESSAGE, null, gamesToContinue, gamesToContinue[0]
        );
        if (continuedGame == null) {
            return;
        } else {
            int userSelect = java.util.Arrays.asList(gamesToContinue).indexOf(continuedGame);
            Game game = main.getExistingGames().get(games.get(userSelect));
            if (game != null) {
                game.addObserver(new HistoryObserver());
                game.addObserver(new PointsObserver(game, this.main.getCurrentUser()));
                game.addObserver(new JsonLogObserver(game, this.main.getCurrentUser(), this.main.getExistingGames(), main.getUsers()));
            }
            this.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                GameFrame gameUi = new GameFrame(main, game);
                gameUi.setVisible(true);
            });
        }
    }
    
    private void processLeaveAccount() {
        this.main.setCurrentUser(null);
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            new LoginAndRegisterWindow(main);
        });
    }

    public void setFreshData() {
        this.userPoints = main.getCurrentUser().getPoints();
        this.activeGames = 0;
        for (Integer gameIdx : main.getExistingGames().keySet()) {
            Game checkGame = main.getExistingGames().get(gameIdx);
            if (checkGame != null && checkGame.getPlayer().getName().equals(main.getCurrentUser().getEmail())){
                this.activeGames++;
            }
        }       
        this.repaint();
        this.revalidate();
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
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        //gametitle
        CustomLabel gameTitle = new CustomLabel("CHESS WAR II", TEXT_COLOR, TEXT_BOLD_BIG);
        gameTitle.setAlignmentX(Component.LEFT_ALIGNMENT);


        //gameSubtitle
        CustomLabel gameSubtitle = new CustomLabel("Main Menu", TEXT_COLOR, TEXT_BOLD_SMALL);
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
        headerRightPart.setLayout(new BoxLayout(headerRightPart, BoxLayout.X_AXIS));
//        headerRightPart.setBackground(BACKGROUND_MEDIUM);
        headerRightPart.setOpaque(false);

        CustomLabel userName = new CustomLabel(this.username, TEXT_COLOR, TEXT_BOLD_MEDIUM);
        userName.setAlignmentX(Component.RIGHT_ALIGNMENT);

        ImageIcon userIconRaw = new ImageIcon(getClass().getResource("/images/user.jpg"));
        JLabel userIcon = new JLabel(userIconRaw);
        userIcon.setAlignmentX(Component.RIGHT_ALIGNMENT);
        userIcon.setBorder(BorderFactory.createEmptyBorder(5,10,0,30));


        headerRightPart.add(userName, BorderLayout.CENTER);
        headerRightPart.add(userIcon, BorderLayout.WEST);

        headerPart.add(headerLeftPart, BorderLayout.WEST);
        headerPart.add(headerRightPart, BorderLayout.EAST);

        return headerPart;
    }
    JPanel drawCentralPart() {
        JPanel centralPart = new JPanel();
        centralPart.setOpaque(false);
        centralPart.setBackground(BACKGROUND_LIGHT);
        centralPart.setLayout(new GridBagLayout());
        centralPart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 630));
        centralPart.setMinimumSize(new Dimension(200, 630));
        centralPart.setPreferredSize(new Dimension(200, 630));

        JPanel centralPartContent = new JPanel();
        centralPartContent.setLayout(new BoxLayout(centralPartContent, BoxLayout.Y_AXIS));
        centralPartContent.setMaximumSize(new Dimension(650, 450));
        centralPartContent.setMinimumSize(new Dimension(650, 450));
        centralPartContent.setBackground(BACKGROUND_LIGHT);

        JPanel upperCentralCardsWrapper = new JPanel();
        upperCentralCardsWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        upperCentralCardsWrapper.setLayout(new BorderLayout());
        upperCentralCardsWrapper.setMaximumSize(new Dimension(650, 150));
        upperCentralCardsWrapper.setMinimumSize(new Dimension(650, 150));
        upperCentralCardsWrapper.setPreferredSize(new Dimension(650, 150));

        JPanel upperLeftCard = new JPanel();
        upperLeftCard.setLayout(new GridBagLayout());
        upperLeftCard.setMinimumSize(new Dimension(300, 100));
        upperLeftCard.setPreferredSize(new Dimension(300, 100));
        upperLeftCard.setMaximumSize(new Dimension(300, 100));
        upperLeftCard.setBackground(BACKGROUND_DARK);
        JPanel upperLeftCardInfoPanel = new JPanel();
        upperLeftCardInfoPanel.setBackground(BACKGROUND_DARK);
        upperLeftCardInfoPanel.setLayout(new BoxLayout(upperLeftCardInfoPanel, BoxLayout.X_AXIS));
        upperLeftCardInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        CustomLabel totalPointsLabel = new CustomLabel(""+this.userPoints, TEXT_COLOR, TEXT_BOLD_MEDIUM);
        totalPointsLabel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

        ImageIcon pointsIconRaw = new ImageIcon(getClass().getResource("/images/pointsIcon.png"));
        JLabel pointsIcon = new JLabel(pointsIconRaw);
        pointsIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        upperLeftCardInfoPanel.add(totalPointsLabel);

        upperLeftCardInfoPanel.add(pointsIcon);


        CustomLabel uperLeftCardInfoLabel = new CustomLabel("Total points: ", BUTTON_COLOR_VIOLET, TEXT_BOLD_MEDIUM);
        uperLeftCardInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        upperLeftCard.add(uperLeftCardInfoLabel);
        upperLeftCard.add(upperLeftCardInfoPanel);

        //right card
        JPanel upperRightCard = new JPanel();
        upperRightCard.setLayout(new GridBagLayout());
        upperRightCard.setMinimumSize(new Dimension(300, 100));
        upperRightCard.setPreferredSize(new Dimension(300, 100));
        upperRightCard.setMaximumSize(new Dimension(300, 100));
        upperRightCard.setBackground(BACKGROUND_DARK);
        JPanel upperRightCardInfoPanel = new JPanel();
        upperRightCardInfoPanel.setBackground(BACKGROUND_DARK);
        upperRightCardInfoPanel.setLayout(new BoxLayout(upperRightCardInfoPanel, BoxLayout.X_AXIS));
        upperRightCardInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        CustomLabel rightTotalPoints = new CustomLabel(""+this.activeGames, TEXT_COLOR, TEXT_BOLD_MEDIUM);
        rightTotalPoints.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        ImageIcon rightPointsIconRaw = new ImageIcon(getClass().getResource("/images/gameIcon.png"));
        JLabel rightPointsIcon = new JLabel(rightPointsIconRaw);
        pointsIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        upperRightCardInfoPanel.add(rightTotalPoints);
        upperRightCardInfoPanel.add(rightPointsIcon);

        CustomLabel uperRightCardInfoLabel = new CustomLabel("Total games: ", BUTTON_COLOR_VIOLET, TEXT_BOLD_MEDIUM);
        uperRightCardInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        upperRightCard.add(uperRightCardInfoLabel);
        upperRightCard.add(upperRightCardInfoPanel);

        upperCentralCardsWrapper.add(upperLeftCard, BorderLayout.WEST);
        upperCentralCardsWrapper.add(upperRightCard, BorderLayout.EAST);
        upperCentralCardsWrapper.setOpaque(false);

        centralPartContent.add(upperCentralCardsWrapper);
        centralPartContent.add(Box.createVerticalStrut(40));

        JPanel buttonsContainer = new JPanel();
        buttonsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsContainer.setLayout(new BoxLayout(buttonsContainer, BoxLayout.Y_AXIS));
        buttonsContainer.setOpaque(false);

        MainMenuClickButton startNewGameButton = new MainMenuClickButton("Start", "Start new game",
                "/images/startGame.png", BACKGROUND_MEDIUM, BACKGROUND_DARK
        );
        startNewGameButton.setMaximumSize(new Dimension(350, 80));
        startNewGameButton.setPreferredSize(new Dimension(350, 80));
        startNewGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        MainMenuClickButton continueGameButton = new MainMenuClickButton("Continue", "Contine one of existing games",
                "/images/replayGame.png", BACKGROUND_MEDIUM, BACKGROUND_DARK
        );
        continueGameButton.setMaximumSize(new Dimension(350, 80));
        continueGameButton.setPreferredSize(new Dimension(350, 80));
        continueGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        MainMenuClickButton leaveAccountButton = new MainMenuClickButton("Logout", "Go back to login page",
                "/images/exitIcon.png", BACKGROUND_MEDIUM, BACKGROUND_DARK
        );
        leaveAccountButton.setMaximumSize(new Dimension(350, 80));
        leaveAccountButton.setPreferredSize(new Dimension(350, 80));
        leaveAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.startButton = startNewGameButton;
        this.restartButton = continueGameButton;
        this.leaveButton = leaveAccountButton;

        buttonsContainer.add(startNewGameButton);
        buttonsContainer.add(Box.createVerticalStrut(15));
        buttonsContainer.add(continueGameButton);
        buttonsContainer.add(Box.createVerticalStrut(15));
        buttonsContainer.add(leaveAccountButton);
        buttonsContainer.add(Box.createVerticalStrut(15));


        centralPartContent.setBorder(BorderFactory.createEmptyBorder(30,0,30,0));
        centralPartContent.add(buttonsContainer);
        centralPart.add(centralPartContent);
        return centralPart;
    }
    private void drawMainMenuFrame() {
        JPanel mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.Y_AXIS));
        mainMenuPanel.setPreferredSize(new Dimension(1100, 700));
        mainMenuPanel.setMinimumSize(new Dimension(1100, 700));
        JPanel headerPart = this.drawHeaderPart();
        headerPart.setBackground(BACKGROUND_MEDIUM);
        mainMenuPanel.add(headerPart);
        mainMenuPanel.add(this.drawCentralPart());
        mainMenuPanel.setBackground(BACKGROUND_LIGHT);
        this.add(mainMenuPanel);
        this.setSize(1100,700);
        this.setVisible(true);
    }
}
