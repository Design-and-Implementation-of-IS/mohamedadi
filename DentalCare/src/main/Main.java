package main;

import boundary.InventoryUI;

import javax.swing.*;
import java.awt.*;


public class Main {
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        
        SwingUtilities.invokeLater(() -> {
            try {
                
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");

                
                InventoryUI ui = new InventoryUI();
                
                
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (screenSize.width - ui.getWidth()) / 2;
                int y = (screenSize.height - ui.getHeight()) / 2;
                ui.setLocation(x, y);
                
                ui.setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to start application: " + e.getMessage());
                JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}

