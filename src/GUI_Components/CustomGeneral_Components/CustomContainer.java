package GUI_Components.CustomGeneral_Components;

import javax.swing.*;
import java.awt.*;

public class CustomContainer extends JPanel {
    public CustomContainer(Color backgroundColor) {
        this.setBackground(backgroundColor);
    }
    public CustomContainer(Color backgroundColor, int padding) {
        this.setBackground(backgroundColor);
        BorderFactory.createEmptyBorder(padding, padding, padding, padding);
    }
}
