package GUI_Components.CustomGeneral_Components;

import javax.swing.*;
import java.awt.*;

import static GUI_Components.CustomGeneral_Components.Global_Style.*;

public class MainMenuClickButton extends ClickButton {
    public MainMenuClickButton(String title, String desc, String pathToIcon, Color base, Color hover) {
        super("", base, hover);
        this.setLayout(new BorderLayout(15,0));
        this.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        ImageIcon rawIcon = new ImageIcon(getClass().getResource(pathToIcon));
        JLabel imageIcon = new JLabel(rawIcon);
        this.add(imageIcon, BorderLayout.WEST);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        CustomLabel titleLabel = new CustomLabel(title, TEXT_COLOR, TEXT_BOLD_MEDIUM);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        CustomLabel descLabel = new CustomLabel(desc, TEXT_COLOR, TEXT_NORMAL_SMALL);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);
        this.add(textPanel, BorderLayout.CENTER);
        JLabel decorLabel = new JLabel(">");
        decorLabel.setFont(TEXT_BOLD_MEDIUM);
        decorLabel.setForeground(Color.GRAY);
        this.add(decorLabel, BorderLayout.EAST);
    }
}