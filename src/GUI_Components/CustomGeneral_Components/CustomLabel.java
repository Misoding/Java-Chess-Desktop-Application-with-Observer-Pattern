package GUI_Components.CustomGeneral_Components;

import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel {
    public CustomLabel( String text, Color defaultColor, Font font) {
        this.setText(text);
        this.setForeground(defaultColor);
        this.setFont(font);
    }
}
