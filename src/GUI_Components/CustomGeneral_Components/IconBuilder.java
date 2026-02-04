    package GUI_Components.CustomGeneral_Components;

    import javax.swing.*;
    import java.awt.*;

    public class IconBuilder {
        public static JLabel iconCreate(String pathToIcon, int width, int height) {
            ImageIcon icon = new ImageIcon(IconBuilder.class.getResource(pathToIcon));
            Image resizedImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(resizedImage));
            return iconLabel;
        }
    }
