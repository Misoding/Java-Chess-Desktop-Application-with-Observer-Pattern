package GUI_Components;

import main_package.Main;
import javax.swing.*;
import java.awt.*;
import static GUI_Components.CustomGeneral_Components.Global_Style.*;

public class LoginAndRegisterWindow extends JFrame {
    private LoginFormComponent loginFormComponent;
    private RegisterFormComponent registerFormComponent;
    private JPanel rightPanel;
    private Main mainApp;

    public LoginAndRegisterWindow(Main mainApp) {
        this.mainApp = mainApp;
        setupWindow();
    }

    private JPanel createGifPanel() {
        JPanel gifPanel = new JPanel();
        gifPanel.setBackground(BACKGROUND_LIGHT);
        gifPanel.setLayout(new GridBagLayout());
        ImageIcon gifImage = new ImageIcon(getClass().getResource("/images/intro_chess.gif"));
        gifPanel.add(new JLabel(gifImage));
        return gifPanel;
    }

    public void displayLoginPanel() {
        this.rightPanel.removeAll();
        if (this.loginFormComponent == null) {
            this.loginFormComponent = new LoginFormComponent(mainApp);
        }
        this.rightPanel.add(this.loginFormComponent);
        this.rightPanel.revalidate();
        this.rightPanel.repaint();
    }
    public void displayRegisterPanel() {
        this.rightPanel.removeAll();
        if (this.registerFormComponent == null) {
            this.registerFormComponent = new RegisterFormComponent(mainApp);
        }
        this.rightPanel.add(this.registerFormComponent);
        this.rightPanel.revalidate();
        this.rightPanel.repaint();
    }
    private void setupWindow() {
        setTitle("Chess - Login & Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setBackground(BACKGROUND_LIGHT);
        JPanel gifPart = this.createGifPanel();
        gifPart.setPreferredSize(new Dimension(550, 700));
        add(gifPart, BorderLayout.WEST);
        loginFormComponent = new LoginFormComponent(this.mainApp);

        JPanel rightPartPanel = new JPanel();
        rightPartPanel.setBackground(BACKGROUND_LIGHT);
        rightPartPanel.setLayout(new GridBagLayout());
        this.rightPanel = rightPartPanel;
        rightPartPanel.add(loginFormComponent);
        add(rightPartPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}