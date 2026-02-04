package Testare;

import GUI_Components.*;
import game_logic.*;
import main_package.Main;
import misc.Enums;
import pieces_details.*;
import user_details.*;

import javax.swing.*;
import java.util.Collections;
import java.util.Scanner;

public class ChessTestRunner {
    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        GUI_Components.CustomGeneral_Components.Global_Style.TEXT_PIECES = new java.awt.Font("Arial Unicode MS", java.awt.Font.BOLD, 25);
        Main.getInstance().read();
        while (true) {
            System.out.println("\nTeste Manuale SAH");
            System.out.println("1.Deschidere 2.Loading 3.Register 4.Login 5.MainMenu");
            System.out.println("6.Board 7.Captura 8.Sah 9.SahMat 10.Promovare 0.Iesire");
            System.out.print("Optiune: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> testDeschidereAplicatie();
                case "2" -> testLoadingComponent();
                case "3" -> testRegister();
                case "4" -> testLogin();
                case "5" -> testMainMenuButtons();
                case "6" -> testBoardInitial();
                case "7" -> testCapturaCalSelectat();
                case "8" -> testSahPeUser();
                case "9" -> testSahMat();
                case "10" -> testPromovarePion();
                case "0" -> {
                    System.out.println("Iesire");
                    return;
                }
                default -> System.out.println("Optiune invalida");
            }
            System.out.println("ENTER pentru continuare");
            scanner.nextLine();
        }
    }
    private static void testDeschidereAplicatie() {
        System.out.println("Deschidere fereastra Login");
        LoginAndRegisterWindow loginWindow = new LoginAndRegisterWindow(Main.getInstance());
        loginWindow.setVisible(true);
        System.out.println("Se inchide in 10 secunde");
        for (int i = 10; i > 0; i--) {
            System.out.println(i);
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        loginWindow.dispose();
        System.out.println("Inchis");
    }
    private static void testLoadingComponent() {
        System.out.println("Afisare loading 5 secunde");
        LoadingComponent loading = new LoadingComponent(Main.getInstance(), 5);
    }
    private static void testRegister() {
        int initialCount = Main.getInstance().getUsers().size();
        System.out.println("Conturi: "+initialCount);
        LoginAndRegisterWindow loginWindow = new LoginAndRegisterWindow(Main.getInstance());
        loginWindow.displayRegisterPanel();
        loginWindow.setVisible(true);
        System.out.println("Completeaza si inchide");
        while (loginWindow.isDisplayable()) {
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        int finalCount = Main.getInstance().getUsers().size();
        if (finalCount > initialCount) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(
                        java.nio.file.Paths.get("src/input/accounts.json")));
                org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
                org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) parser.parse(content);
                org.json.simple.JSONObject lastAccount = (org.json.simple.JSONObject) jsonArray.get(jsonArray.size()-1);
                System.out.println("Cont nou: "+lastAccount.toJSONString());
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            System.out.println("Niciun cont nou");
        }
    }
    private static void testLogin() {
        String testEmail = "test_login@test.com";
        String testPassword = "parola123";
        System.out.println("Cont: "+testEmail+" | "+testPassword);
        User existingUser = Main.getInstance().dbEmailUserSearch(testEmail);
        if (existingUser == null) {
            Main.getInstance().newAccount(testEmail, testPassword);
            Main.getInstance().write();
        }
        LoginAndRegisterWindow loginWindow = new LoginAndRegisterWindow(Main.getInstance());
        loginWindow.setVisible(true);
        System.out.println("Logheaza-te si inchide");
        while (loginWindow.isDisplayable()) {
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        User currentUser = Main.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail().equals(testEmail)) {
            System.out.println("Login reusit: "+currentUser.getEmail());
        } else {
            System.out.println("Login nereusit");
        }
    }
    private static void testMainMenuButtons() {
        String testEmail = "test_mainmenu@test.com";
        User existingUser = Main.getInstance().dbEmailUserSearch(testEmail);
        if (existingUser == null) {
            Main.getInstance().newAccount(testEmail, "parola123");
            Main.getInstance().write();
        }
        Main.getInstance().setCurrentUser(Main.getInstance().dbEmailUserSearch(testEmail));
        User currentUser = Main.getInstance().getCurrentUser();
        if (currentUser.getActiveGamesIds().isEmpty()) {
            int id = Main.getInstance().getExistingGames().isEmpty() ? 1 :
                    Collections.max(Main.getInstance().getExistingGames().keySet())+1;
            Game game = new Game(id);
            Player player = new Player(currentUser.getEmail(), Enums.Colors.WHITE);
            Player opponent = new Player("Computer", Enums.Colors.BLACK);
            game.setPlayer(player);
            game.setOpponent(opponent);
            game.setPlayerId(Main.getInstance().getUsers().indexOf(currentUser));
            game.setOpponentId(-1);
            game.start();
            currentUser.addGame(game);
            Main.getInstance().getExistingGames().put(id, game);
            Main.getInstance().write();
            System.out.println("Joc creat ID:"+id);
        }
        System.out.println("Testeaza butoanele");
        MainMenuFrame mainMenu = new MainMenuFrame(Main.getInstance());
        mainMenu.setVisible(true);
        while (mainMenu.isDisplayable()) {
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
    private static void testBoardInitial() {
        setupTestUser("test_board@test.com");
        Board board = new Board();
        board.initialize();
        System.out.println("Board: "+board.getBoardPieces().size()+" piese");
        openGameWithBoard(board, Enums.Colors.WHITE, null);
    }
    private static void testCapturaCalSelectat() {
        setupTestUser("test_captura@test.com");
        Board board = new Board();
        board.initialize();
        Piece knight = board.getPieceAt(new Position('B', 1));
        board.removePiece(new Position('B', 1));
        knight.setPosition(new Position('C', 3));
        board.placePieceAt(new Position('C', 3), knight);
        board.placePieceAt(new Position('D', 5), new Pawn(Enums.Colors.BLACK, new Position('D', 5)));
        board.placePieceAt(new Position('E', 4), new Pawn(Enums.Colors.BLACK, new Position('E', 4)));
        board.refreshPieces();
        System.out.println("Cal C3 poate captura D5 sau E4");
        openGameWithBoard(board, Enums.Colors.WHITE, new Position('C', 3));
    }
    private static void testSahPeUser() {
        setupTestUser("test_sah@test.com");
        Board board = new Board();
        board.placePieceAt(new Position('E', 1), new King(Enums.Colors.WHITE, new Position('E', 1)));
        board.placePieceAt(new Position('D', 2), new Pawn(Enums.Colors.WHITE, new Position('D', 2)));
        board.placePieceAt(new Position('F', 2), new Pawn(Enums.Colors.WHITE, new Position('F', 2)));
        board.placePieceAt(new Position('A', 2), new Rook(Enums.Colors.WHITE, new Position('A', 2)));
        board.placePieceAt(new Position('E', 8), new King(Enums.Colors.BLACK, new Position('E', 8)));
        board.placePieceAt(new Position('E', 5), new Queen(Enums.Colors.BLACK, new Position('E', 5)));
        board.refreshPieces();
        System.out.println("SAH Regina E5 ataca Regele E1");
        System.out.println("Muta regele sau blocheaza cu tura A2->E2");
        openGameWithBoard(board, Enums.Colors.WHITE, null);
    }
    private static void testSahMat() {
        setupTestUser("test_sahmat@test.com");
        Board board = new Board();
        board.placePieceAt(new Position('F', 6), new King(Enums.Colors.WHITE, new Position('F', 6)));
        board.placePieceAt(new Position('A', 1), new Queen(Enums.Colors.WHITE, new Position('A', 1)));
        board.placePieceAt(new Position('B', 1), new Rook(Enums.Colors.WHITE, new Position('B', 1)));
        board.placePieceAt(new Position('H', 8), new King(Enums.Colors.BLACK, new Position('H', 8)));
        board.placePieceAt(new Position('G', 7), new Pawn(Enums.Colors.BLACK, new Position('G', 7)));
        board.placePieceAt(new Position('H', 7), new Pawn(Enums.Colors.BLACK, new Position('H', 7)));
        board.refreshPieces();
        System.out.println("Rege negru H8 blocat de pioni G7 H7");
        System.out.println("Muta Tura B1->B8 SAH MAT");
        openGameWithBoard(board, Enums.Colors.WHITE, new Position('B', 1));
    }
    private static void testPromovarePion() {
        setupTestUser("test_promovare@test.com");
        Board board = new Board();
        board.placePieceAt(new Position('E', 1), new King(Enums.Colors.WHITE, new Position('E', 1)));
        board.placePieceAt(new Position('D', 7), new Pawn(Enums.Colors.WHITE, new Position('D', 7)));
        board.placePieceAt(new Position('E', 8), new King(Enums.Colors.BLACK, new Position('E', 8)));
        board.refreshPieces();
        System.out.println("Pion D7 muta D7->D8 promovare");
        openGameWithBoard(board, Enums.Colors.WHITE, new Position('D', 7));
    }
    private static void setupTestUser(String email) {
        User existingUser = Main.getInstance().dbEmailUserSearch(email);
        if (existingUser == null) {
            Main.getInstance().newAccount(email, "parola123");
            Main.getInstance().write();
        }
        Main.getInstance().setCurrentUser(Main.getInstance().dbEmailUserSearch(email));
    }
    private static void openGameWithBoard(Board board, Enums.Colors playerColor, Position selectPiece) {
        Player player = new Player("TestPlayer", playerColor);
        Enums.Colors opponentColor = playerColor == Enums.Colors.WHITE ?
                Enums.Colors.BLACK : Enums.Colors.WHITE;
        Player opponent = new Player("CPU", opponentColor);
        Game game = new Game(999);
        game.setPlayer(player);
        game.setOpponent(opponent);
        game.setBoard(board);
        game.setAttackOrder(playerColor == Enums.Colors.WHITE ? 0 : 1);
        GameFrame gameFrame = new GameFrame(Main.getInstance(), game);
        if (selectPiece != null) {
            gameFrame.selectPieceAt(selectPiece);
        }
        gameFrame.setVisible(true);
    }
}