package com.gimnasio.util;

import javax.swing.*;
import java.awt.*;

public class SwingUtil {
    private SwingUtil() {}

    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(180, 28));
        return field;
    }

    public static JButton button(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
