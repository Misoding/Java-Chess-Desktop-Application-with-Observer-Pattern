package GUI_Components;

import GUI_Components.CustomGeneral_Components.CustomLabel;
import main_package.Main;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;

import java.awt.*;

import static GUI_Components.CustomGeneral_Components.Global_Style.*;

public class LoadingComponent extends JWindow{
    private JProgressBar progressBar;
    private CustomLabel progressLabel;
    private JPanel loadingPanel;
    private JPanel gifPanel;
    private Main redirectApp;
    private int loadingTime;

    public LoadingComponent(Main redirectApp, int loadingTime){
        this.redirectApp = redirectApp;
        this.loadingTime = loadingTime;
        this.drawComponent();
    }
    private void createGifPanel() {
        this.gifPanel = new JPanel();
        this.gifPanel.setBackground(BACKGROUND_LIGHT);
        ImageIcon gifImage = new ImageIcon(getClass().getResource("/images/intro_chess.gif"));
        this.gifPanel.add(new JLabel(gifImage), BorderLayout.CENTER);
    }
    private void drawComponent(){
        this.createGifPanel();
        setLayout(new BorderLayout());
        setSize(700,490);
        setLocationRelativeTo(null);
        loadingPanel = new JPanel();
        //Loading panel
        loadingPanel.setLayout(new BorderLayout());
        loadingPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // Progress Label
        progressLabel = new CustomLabel("Loading...", TEXT_COLOR, TEXT_BOLD_MEDIUM);
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(450, 30));
        progressBar.setOpaque(true);
        progressBar.setUI(new BasicProgressBarUI() {
            protected Color getSelectionBackground() {
                return TEXT_COLOR;
            }
            protected Color getSelectionForeground() {
                return TEXT_COLOR;
            }
        });
        progressBar.setBorderPainted(false);
        progressBar.setBackground(BACKGROUND_DARK);
        progressBar.setForeground(BUTTON_COLOR_ORANGE);
        progressBar.setStringPainted(true);
        progressBar.setString("0");
        JPanel progressBarWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressBarWrapper.setBackground(BACKGROUND_LIGHT);
        progressBarWrapper.add(progressBar);

        loadingPanel.add(progressLabel, BorderLayout.NORTH);
        loadingPanel.add(progressBarWrapper, BorderLayout.CENTER);
        loadingPanel.setBackground(BACKGROUND_LIGHT);
        add(gifPanel, BorderLayout.CENTER);
        add(loadingPanel, BorderLayout.SOUTH);
        this.setVisible(true);
        this.activeLoading(this.loadingTime);
    }
    private void activeLoading(int seconds) {
        String[] loadingVault = {
                "Initializare date...",
                "Incarcare utilizatori...",
                "Incarcarea meciurilor...",
                "Setarea preferintelor...",
                "Conectarea la chess global center...",
                "Finalizare incarcare..."
        };
        int timeToPassMs = seconds * 1000;
        int changeInterval = 30;
        long startTime = System.currentTimeMillis();
        javax.swing.Timer timer = new javax.swing.Timer(changeInterval, e -> {
            long passed = System.currentTimeMillis() - startTime;
            int timeToProgress = (int) Math.min((passed * 100.0) / timeToPassMs, 100);
            int textIndex = Math.min((timeToProgress * loadingVault.length) / 100, loadingVault.length - 1);
            progressBar.setValue(timeToProgress);
            progressBar.setString(timeToProgress + "%");
            progressLabel.setText(loadingVault[textIndex]);
            if (timeToProgress >= 100) {
                ((javax.swing.Timer) e.getSource()).stop();
                this.closeLoadingGoNext();
            }
        });
        timer.start();
    }
    private void closeLoadingGoNext() {
        SwingUtilities.invokeLater(() -> {
            this.dispose();
            LoginAndRegisterWindow loginWindow = new LoginAndRegisterWindow(this.redirectApp);
            loginWindow.setVisible(true);
        });
    }
}
