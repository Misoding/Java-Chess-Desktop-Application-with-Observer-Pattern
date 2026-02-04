package GUI_Components.CustomGeneral_Components;

import javax.swing.*;
import java.awt.*;

import static GUI_Components.CustomGeneral_Components.Global_Style.*;

public class UserNotifyHelper {
    public static void displayError(Component parent, String msg, String title) {
        universalDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE, BUTTON_COLOR_RED);
    }
    public static void displayInfo(Component parent, String msg, String title) {
        universalDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE, BUTTON_COLOR_BLUE);
    }
    public static void displayWarrning(Component parent, String msg, String title) {
        universalDialog(parent, msg, title, JOptionPane.WARNING_MESSAGE, BUTTON_COLOR_ORANGE);
    }
    public static void universalDialog(Component parent, String msg,
                                       String title, int msgType, Color buttonColor) {
        UIManager.put("OptionPane.background", buttonColor);
        UIManager.put("Panel.background", BACKGROUND_MEDIUM);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        JOptionPane.showMessageDialog(parent,msg,title,msgType);
    }
}
