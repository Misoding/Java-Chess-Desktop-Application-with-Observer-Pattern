package GUI_Components;

import GUI_Components.CustomGeneral_Components.ClickButton;
import GUI_Components.CustomGeneral_Components.CustomLabel;
import GUI_Components.CustomGeneral_Components.UserNotifyHelper;
import main_package.Main;
import user_details.User;

import javax.swing.*;
import java.awt.*;

import static GUI_Components.CustomGeneral_Components.Global_Style.*;

public class LoginFormComponent extends JPanel {

    private CustomLabel titleText;
    private CustomLabel titleDescriptionText;
    private JPanel loginFormPanel;

    private JTextField emailInput;
    private JPasswordField passwordInput;
    private ClickButton loginButton;
    private ClickButton registerButton;
    
    private Main main;
    public LoginFormComponent(Main main) {
        this.main = main;
        this.drawLoginFormComponent();
        this.loginButton.addActionListener(e -> processLoginClick());
        this.registerButton.addActionListener(e -> processRegisterRedirectClick());
    }
    private void processLoginClick() {
        String email = this.emailInput.getText().trim();
        String password = new String(this.passwordInput.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            UserNotifyHelper.displayError(this,"Try again, enter your email and password",
                    "Empty email or password field");
            return;
        }
        User user = main.dbEmailUserSearch(email);
        if (user != null && user.getPassword().equals(password)) {
            main.setCurrentUser(user);
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            parent.dispose();
            SwingUtilities.invokeLater(() -> {
                new MainMenuFrame(main);
            });
            UserNotifyHelper.displayInfo(this, "You successfully logged in your account",
                    "Success log in");
        }
        else {
            UserNotifyHelper.displayError(this, "Wrong email or password, try again...",
                    "Wrong credentials");
            passwordInput.setText("");
        }
    }
    private void processRegisterRedirectClick() {
        LoginAndRegisterWindow parent = (LoginAndRegisterWindow) SwingUtilities.getWindowAncestor(this);
        parent.displayRegisterPanel();
    }
    private JPanel buildHeaderPart() {
        JPanel headerPart = new JPanel();

        headerPart.setBackground(BACKGROUND_LIGHT);
        headerPart.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPart.setLayout(new BorderLayout());
        this.titleText = new CustomLabel("Login Form", TEXT_COLOR, TEXT_BOLD_BIG);
        this.titleText.setHorizontalAlignment(JLabel.CENTER);
        this.titleText.setBorder(BorderFactory.createEmptyBorder(5,0,10,0));

        this.titleDescriptionText = new CustomLabel("Enter your email and password to log in",TEXT_COLOR, TEXT_BOLD_SMALL);
        this.titleDescriptionText.setHorizontalAlignment(JLabel.CENTER);

        headerPart.add(this.titleText, BorderLayout.NORTH);
        headerPart.add(this.titleDescriptionText,  BorderLayout.CENTER);
        return headerPart;

    }
    private JPanel buildSeparatorPart() {
        JPanel separatorPart = new JPanel();
        separatorPart.setBackground(BACKGROUND_LIGHT);
        separatorPart.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel leftSeparatorPanel =  new JPanel();
        leftSeparatorPanel.setLayout(new BorderLayout());
        JSeparator leftSeparator = new JSeparator(JSeparator.HORIZONTAL);
        leftSeparator.setForeground(BUTTON_COLOR_VIOLET);
        leftSeparatorPanel.add(leftSeparator, BorderLayout.CENTER);
        leftSeparatorPanel.setPreferredSize(new Dimension(90,2));


        CustomLabel centralText = new CustomLabel("OR", TEXT_COLOR, TEXT_BOLD_SMALL);
        centralText.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel rightSeparatorPanel =  new JPanel();
        rightSeparatorPanel.setLayout(new BorderLayout());
        JSeparator rightSeparator = new JSeparator(JSeparator.HORIZONTAL);
        rightSeparator.setForeground(BUTTON_COLOR_VIOLET);
        rightSeparator.setBackground(TEXT_COLOR);
        rightSeparatorPanel.add(rightSeparator, BorderLayout.CENTER);
        rightSeparatorPanel.setPreferredSize(new Dimension(90,2));


        separatorPart.add(leftSeparatorPanel, BorderLayout.WEST);
        separatorPart.add(centralText, BorderLayout.CENTER);
        separatorPart.add(rightSeparatorPanel, BorderLayout.EAST);
        return separatorPart;
    }
    private JPanel buildRegisterPart() {
        JPanel registerPartPanel = new JPanel();
        registerPartPanel.setLayout(new BoxLayout(registerPartPanel, BoxLayout.Y_AXIS));
        registerPartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ClickButton registerOptionButton = new ClickButton("Register",
                BUTTON_COLOR_VIOLET, BUTTON_COLOR_VIOLET_HOVER);
        registerOptionButton.setPreferredSize(new Dimension(150, 30));
        registerOptionButton.setMaximumSize(new Dimension(150, 30));
        JPanel registerButtonPanel = new JPanel();
        registerButtonPanel.setLayout(new FlowLayout());
        registerButtonPanel.add(registerOptionButton);
        registerButtonPanel.setBackground(BACKGROUND_LIGHT);
        registerPartPanel.add(registerButtonPanel);

        this.registerButton = registerOptionButton;
        return registerPartPanel;
    }
    private JPanel buildLoginFormPanel() {
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setBackground(BACKGROUND_LIGHT);
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
        loginFormPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        CustomLabel emailLabel = new CustomLabel("EMAIL ADRESS", TEXT_COLOR, TEXT_BOLD_SMALL);
        emailLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        emailLabel.setPreferredSize(new Dimension(350, 30));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setHorizontalAlignment(SwingConstants.LEFT);
        emailLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));


        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(350, 35));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setCaretColor(TEXT_COLOR);
        emailField.setFont(TEXT_BOLD_SMALL);
        emailField.setBackground(BACKGROUND_MEDIUM);
        emailField.setForeground(TEXT_COLOR);
        emailField.setBorder(BorderFactory.createLineBorder(BACKGROUND_DARK,2));
        this.emailInput = emailField;

        loginFormPanel.add(emailLabel);
        loginFormPanel.add(emailField);

        CustomLabel passwordLabel = new CustomLabel("PASSWORD", TEXT_COLOR, TEXT_BOLD_SMALL);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
        passwordLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordLabel.setPreferredSize(new Dimension(350, 30));
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(350, 35));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passwordField.setMinimumSize(new Dimension(150, 35));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setCaretColor(TEXT_COLOR);
        passwordField.setFont(TEXT_BOLD_SMALL);
        passwordField.setBackground(BACKGROUND_MEDIUM);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createLineBorder(BACKGROUND_DARK,2));
        this.passwordInput = passwordField;

        loginFormPanel.add(passwordLabel);
        loginFormPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonPanel.setLayout(new FlowLayout());
        ClickButton loginFormButton = new ClickButton("Login", BUTTON_COLOR_ORANGE, BUTTON_HOVER_COLOR);
        loginFormButton.setMaximumSize(new Dimension(150, 30));
        loginFormButton.setPreferredSize(new Dimension(150, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,5,0));
        buttonPanel.setBackground(BACKGROUND_LIGHT);
        this.loginButton = loginFormButton;

        buttonPanel.add(loginFormButton);
        loginFormPanel.add(buttonPanel);


        return loginFormPanel;
    }
    private JPanel buildLoginBlock(){
        this.loginFormPanel =  this.buildLoginFormPanel();
        JPanel registerPartPanel = this.buildRegisterPart();
        JPanel registerSeparatorPart = this.buildSeparatorPart();
        JPanel headerPartPanel = buildHeaderPart();

        JPanel loginBlock = new JPanel();
        loginBlock.setBackground(BACKGROUND_LIGHT);
        loginBlock.setLayout(new BoxLayout(loginBlock, BoxLayout.Y_AXIS));

        loginBlock.add(headerPartPanel);
        loginBlock.add(this.loginFormPanel);
        loginBlock.add(registerSeparatorPart);
        loginBlock.add(registerPartPanel);

        return loginBlock;

    }
    private void drawLoginFormComponent() {
        JPanel loginBlock = buildLoginBlock();
        this.setLayout(new GridBagLayout());
        this.add(loginBlock);
        this.setBackground(BACKGROUND_LIGHT);
        loginBlock.setPreferredSize(new Dimension(350,350));
        loginBlock.setMaximumSize(new Dimension(350,350));
        this.setVisible(true);
    }
}
