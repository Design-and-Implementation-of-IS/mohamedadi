package main;

import boundary.InventoryUI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Launch the DentalCare Inventory GUI
        SwingUtilities.invokeLater(() -> new InventoryUI());
    }
}

