package GUI_Components.CustomGeneral_Components;

import misc.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static GUI_Components.CustomGeneral_Components.Global_Style.*;

public class ChessButton extends JButton {
    private int i;
    private int j;
    private boolean colorSide;
    private Color normalColor;
    private Color hoverColor;
    private boolean clicked;
    public ChessButton(int i, int j) {
        this.i = i;
        this.j = j;
        this.clicked = false;
        this.colorSide = ((i + j) % 2 == 0);
        if (colorSide) {
            this.normalColor = BOARD_LIGHT_COLOR;
            this.hoverColor = BOARD_LIGHT_COLOR_HOVER;
        } else {
            this.normalColor = BOARD_DARK_COLOR;
            this.hoverColor = BOARD_DARK_COLOR_HOVER;
        }
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setOpaque(true);
        this.setBackground(this.normalColor);
        this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!clicked) {
                    setBackground(hoverColor);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!clicked) {
                    setBackground(normalColor);
                }
            }
        });
    }
    public void paintBack() {
        this.setBackground(this.normalColor);
        this.clicked = false;
    }
    public void specialPaint(Color color) {
        this.setBackground(color);
        this.clicked = true;
    }
    public int geti() {
        return this.i;
    }
    public int getj() {
        return this.j;
    }

}
