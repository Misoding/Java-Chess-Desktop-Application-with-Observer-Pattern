package GUI_Components;

import GUI_Components.CustomGeneral_Components.ClickButton;
import GUI_Components.CustomGeneral_Components.CustomLabel;
import GUI_Components.CustomGeneral_Components.UserNotifyHelper;
import main_package.Main;
import user_details.User;

import javax.swing.*;
import java.awt.*;

import static GUI_Components.CustomGeneral_Components.Global_Style.*;

public class RegisterFormComponent extends JPanel {

    private CustomLabel titleText;
    private CustomLabel titleDescriptionText;
    private JPanel loginFormPanel;

    private JTextField emailInput;
    private JPasswordField passwordInput;
    private JPasswordField repeatPasswordInput;
    private ClickButton loginButton;
    private ClickButton registerButton;

    private Main main;
    public RegisterFormComponent(Main main) {
        this.main = main;
        this.drawLoginFormComponent();
        this.loginButton.addActionListener(e -> processRegisterClick());
        this.registerButton.addActionListener(e -> processLoginRedirectClick());
    }
    private void processRegisterClick() {
        String email = this.emailInput.getText().trim();
        String password = new String(this.passwordInput.getPassword());
        String passwordRepeat = new String(repeatPasswordInput.getPassword());
        if (email.isEmpty() || password.isEmpty() ||  passwordRepeat.isEmpty()) {
            UserNotifyHelper.displayError(this,"Try again, enter your email and password",
                    "Empty email or password field");
            this.passwordInput.setText("");
            this.repeatPasswordInput.setText("");
            return;
        }
        if (password.equals(passwordRepeat)) {
            if (!email.contains("@")) {
                UserNotifyHelper.displayWarrning(this,"Please enter valid email adress",
                        "Wrong email address");
                return;
            }
            if (password.length() < 6) {
                UserNotifyHelper.displayWarrning(this, "Password must be longer than 6 characters",
                        "Short password");
                return;
            }
            User newUser = main.newAccount(email, password);
            if (newUser == null) {
                UserNotifyHelper.displayWarrning(this, "Email already taken",
                        "Email already taken");
                this.emailInput.setText("");
                this.repeatPasswordInput.setText("");
                this.passwordInput.setText("");
                return;
            }
            main.write();
            UserNotifyHelper.displayInfo(this, "Account successfully created!",
                    "Successfully created account");
            LoginAndRegisterWindow parent = (LoginAndRegisterWindow) SwingUtilities.getWindowAncestor(this);
            parent.displayLoginPanel();

        } else {
            UserNotifyHelper.displayError(this, "Password don't match with repeat one",
                    "Passwords don't match");
            this.repeatPasswordInput.setText("");
            this.passwordInput.setText("");
            return;
        }
    }
    private void processLoginRedirectClick() {
        LoginAndRegisterWindow parent = (LoginAndRegisterWindow) SwingUtilities.getWindowAncestor(this);
        parent.displayLoginPanel();
    }
    private JPanel buildHeaderPart() {
        JPanel headerPart = new JPanel();

        headerPart.setBackground(BACKGROUND_LIGHT);
        headerPart.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPart.setLayout(new BorderLayout());
        this.titleText = new CustomLabel("Register Form", TEXT_COLOR, TEXT_BOLD_BIG);
        this.titleText.setHorizontalAlignment(JLabel.CENTER);
        this.titleText.setBorder(BorderFactory.createEmptyBorder(5,0,10,0));

        this.titleDescriptionText = new CustomLabel("Enter your email and password to register",TEXT_COLOR, TEXT_BOLD_SMALL);
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
        ClickButton registerOptionButton = new ClickButton("Login",
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

        CustomLabel repeatPasswordLabel = new CustomLabel("REPEAT PASSWORD", TEXT_COLOR, TEXT_BOLD_SMALL);
        repeatPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        repeatPasswordLabel.setHorizontalAlignment(SwingConstants.LEFT);
        repeatPasswordLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        repeatPasswordLabel.setPreferredSize(new Dimension(350, 30));
        repeatPasswordLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        JPasswordField repeatPasswordField = new JPasswordField();
        repeatPasswordField.setPreferredSize(new Dimension(350, 35));
        repeatPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        repeatPasswordField.setMinimumSize(new Dimension(150, 35));
        repeatPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        repeatPasswordField.setCaretColor(TEXT_COLOR);
        repeatPasswordField.setFont(TEXT_BOLD_SMALL);
        repeatPasswordField.setBackground(BACKGROUND_MEDIUM);
        repeatPasswordField.setForeground(TEXT_COLOR);
        repeatPasswordField.setBorder(BorderFactory.createLineBorder(BACKGROUND_DARK,2));
        this.repeatPasswordInput = repeatPasswordField;

        loginFormPanel.add(passwordLabel);
        loginFormPanel.add(passwordField);
        loginFormPanel.add(repeatPasswordLabel);
        loginFormPanel.add(repeatPasswordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonPanel.setLayout(new FlowLayout());
        ClickButton loginFormButton = new ClickButton("Register", BUTTON_COLOR_ORANGE, BUTTON_HOVER_COLOR);
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
        loginBlock.setPreferredSize(new Dimension(350,400));
        loginBlock.setMaximumSize(new Dimension(350,400));
        this.setVisible(true);
    }
}
