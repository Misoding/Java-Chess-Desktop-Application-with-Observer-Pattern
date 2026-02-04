package GUI_Components.CustomGeneral_Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static GUI_Components.CustomGeneral_Components.Global_Style.TEXT_BOLD_MEDIUM;
import static GUI_Components.CustomGeneral_Components.Global_Style.TEXT_COLOR;

public class ClickButton extends JButton {
    private Color defaultColor;
    private Color hoverColor;
    public ClickButton(String text, Color defaultColor, Color hoverColor) {
        super(text);
        this.defaultColor = defaultColor;
        this.hoverColor = hoverColor;
        this.setFont(TEXT_BOLD_MEDIUM);
        this.setBackground(defaultColor);
        this.setForeground(TEXT_COLOR);
        this.setFocusPainted(false);
        this.setOpaque(true);
        this.setBorderPainted(false);
        this.setContentAreaFilled(true);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                setBackground(defaultColor);
            }
        });
    }
}
