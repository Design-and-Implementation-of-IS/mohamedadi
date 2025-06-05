package boundary;

import control.InventoryManager;
import control.SupirXMLImporter;
import entity.Item;
import entity.Supplier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

public class InventoryUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final InventoryManager manager;
    private final JTextArea outputArea;

    private final JTextField tfSerialNum = new JTextField(10);
    private final JTextField tfItemName = new JTextField(10);
    private final JTextField tfDescription = new JTextField(10);
    private final JTextField tfQuantity = new JTextField(10);
    private final JTextField tfSupplierId = new JTextField(10);
    private final JTextField tfExpDate = new JTextField(10);

    private final JTextField tfUpdateSerial = new JTextField(10);
    private final JTextField tfUpdateQuantity = new JTextField(10);
    private final JTextField tfUpdateSupplierId = new JTextField(10);

    public InventoryUI() {
        super("DentalCare Inventory System");

        manager = new InventoryManager();
        XMLParser parser = new XMLParser();
        SupirXMLImporter importer = new SupirXMLImporter(parser, manager);

        // === Buttons ===
        JButton btnImportXML = new JButton("\ud83d\udce5 Import from XML");
        JButton btnShowAlerts = new JButton("\u26a0 Show Inventory Alerts");
        JButton btnAddItem = new JButton("\u2795 Add New Item");
        JButton btnUpdateItem = new JButton("\u270f Update Quantity / Supplier");
        JButton btnShowSuppliers = new JButton("\ud83d\udccb Show All Suppliers");
        JButton btnExportXML = new JButton("\ud83d\udce4 Export Inventory to XML");

        // === Output area ===
        outputArea = new JTextArea(18, 65);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // === Input panel ===
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 6, 4));
        inputPanel.setBorder(BorderFactory.createTitledBorder("\u2795 Add New Item"));

        inputPanel.add(new JLabel("Serial Number:"));
        inputPanel.add(tfSerialNum);
        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(tfItemName);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(tfDescription);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(tfQuantity);
        inputPanel.add(new JLabel("Supplier ID:"));
        inputPanel.add(tfSupplierId);
        inputPanel.add(new JLabel("Exp. Date (YYYY-MM-DD):"));
        inputPanel.add(tfExpDate);

        // === Update panel ===
        JPanel updatePanel = new JPanel(new GridLayout(0, 2, 6, 4));
        updatePanel.setBorder(BorderFactory.createTitledBorder("\u270f Update Item"));

        updatePanel.add(new JLabel("Serial Number:"));
        updatePanel.add(tfUpdateSerial);
        updatePanel.add(new JLabel("New Quantity:"));
        updatePanel.add(tfUpdateQuantity);
        updatePanel.add(new JLabel("New Supplier ID:"));
        updatePanel.add(tfUpdateSupplierId);

        // === Button panel ===
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        buttonPanel.add(btnImportXML);
        buttonPanel.add(btnShowAlerts);
        buttonPanel.add(btnAddItem);
        buttonPanel.add(btnUpdateItem);
        buttonPanel.add(btnShowSuppliers);
        buttonPanel.add(btnExportXML);

        // === Actions ===
        btnImportXML.addActionListener((ActionEvent e) -> {
            try {
                importer.importFromSupir("src/supir_items.xml");
                showMessage("\u2705 XML import completed.");
            } catch (Exception ex) {
                showError("\u274c Failed to import XML:\n" + ex.getMessage());
            }
        });

        btnShowAlerts.addActionListener((ActionEvent e) -> {
            List<Item> alerts = manager.generateAlerts();
            if (alerts.isEmpty()) {
                showMessage("\u2705 No low-stock items found.");
            } else {
                output("=== Inventory Alerts ===\n");
                for (Item item : alerts) output(item + "\n");
            }
        });

        btnAddItem.addActionListener((ActionEvent e) -> {
            try {
                int serial = Integer.parseInt(tfSerialNum.getText());
                String name = tfItemName.getText();
                String desc = tfDescription.getText();
                int qty = Integer.parseInt(tfQuantity.getText());
                int supplierId = Integer.parseInt(tfSupplierId.getText());
                LocalDate exp = LocalDate.parse(tfExpDate.getText());

                Supplier s = new Supplier(String.valueOf(supplierId), "Manual", "", "", "");
                manager.addSupplier(s);
                Item item = new Item(serial, name, desc, null, qty, exp, s);
                manager.addItem(item);
                showMessage("\u2705 Item added: " + item.getName());
            } catch (Exception ex) {
                showError("\u274c Error adding item:\n" + ex.getMessage());
            }
        });

        btnUpdateItem.addActionListener((ActionEvent e) -> {
            try {
                int serial = Integer.parseInt(tfUpdateSerial.getText());

                if (!tfUpdateQuantity.getText().isEmpty()) {
                    int qty = Integer.parseInt(tfUpdateQuantity.getText());
                    manager.updateStock(serial, qty);
                }

                if (!tfUpdateSupplierId.getText().isEmpty()) {
                    int sid = Integer.parseInt(tfUpdateSupplierId.getText());
                    Supplier s = new Supplier(String.valueOf(sid), "Manual", "", "", "");
                    manager.addSupplier(s);
                    manager.assignSupplier(serial, s);
                }

                showMessage("\u2705 Update complete for item " + serial);
            } catch (Exception ex) {
                showError("\u274c Failed to update:\n" + ex.getMessage());
            }
        });

        btnShowSuppliers.addActionListener((ActionEvent e) -> {
            List<Supplier> suppliers = AccessLoader.loadSuppliersAsList();
            if (suppliers.isEmpty()) {
                showMessage("No suppliers found.");
            } else {
                output("=== Suppliers ===\n");
                for (Supplier s : suppliers) output(s + "\n");
            }
        });

        btnExportXML.addActionListener((ActionEvent e) -> {
            try {
                List<Item> items = AccessLoader.loadItems();
                FileWriter writer = new FileWriter("exported_inventory.xml");
                writer.write("<Inventory>\n");
                for (Item item : items) {
                    writer.write("  <Item>\n");
                    writer.write("    <serialNum>" + item.getId() + "</serialNum>\n");
                    writer.write("    <itemName>" + item.getName() + "</itemName>\n");
                    writer.write("    <description>" + item.getDescription() + "</description>\n");
                    writer.write("    <quantityInStock>" + item.getQuantity() + "</quantityInStock>\n");
                    writer.write("    <supplierId>" + (item.getSupplier() != null ? item.getSupplier().getId() : "") + "</supplierId>\n");
                    writer.write("    <expDate>" + (item.getExpiryDate() != null ? item.getExpiryDate() : "") + "</expDate>\n");
                    writer.write("  </Item>\n");
                }
                writer.write("</Inventory>");
                writer.close();
                showMessage("\u2705 Exported to exported_inventory.xml");
            } catch (Exception ex) {
                showError("\u274c Export failed:\n" + ex.getMessage());
            }
        });

        // === Layout ===
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(updatePanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout(10, 10));
        add(centerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void output(String text) {
        outputArea.append(text);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "DentalCare", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

