package boundary;

import control.InventoryManager;
import control.SupirXMLImporter;
import entity.Item;
import entity.Supplier;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Main UI class for the DentalCare Inventory System.
 * Provides interface for managing inventory items and suppliers.
 */
public class InventoryUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String TITLE = "DentalCare Inventory System";
    private static final int PADDING = 6;
    private static final int FORM_PADDING = 8;
    private static final int TEXT_FIELD_SIZE = 15;
    private static final Font MONOSPACE_FONT = new Font("Monospaced", Font.PLAIN, 12);
    private static final Dimension BUTTON_SIZE = new Dimension(140, 25);
    private static final Dimension TEXT_FIELD_SIZE_DIM = new Dimension(140, 22);
    private static final Color BORDER_COLOR = new Color(180, 180, 180);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color OUTPUT_BACKGROUND = new Color(252, 252, 252);
    private static final Color HEADER_BACKGROUND = new Color(240, 240, 240);
    private static final Color SEPARATOR_COLOR = new Color(220, 220, 220);
    private static final Font OUTPUT_FONT = new Font("Consolas", Font.PLAIN, 12);
    private static final Font HEADER_FONT = new Font("Consolas", Font.BOLD, 12);
    
    // UI Components
    private final JTextArea outputArea;
    private final InventoryManager manager;
    
    // Input Fields
    private final JTextField tfSerialNum;
    private final JTextField tfItemName;
    private final JTextField tfDescription;
    private final JTextField tfQuantity;
    private final JTextField tfSupplierId;
    private final JTextField tfExpDate;
    
    // Update Fields
    private final JTextField tfUpdateSerial;
    private final JTextField tfUpdateQuantity;
    private final JTextField tfUpdateSupplierId;
    
    // Table Components
    private final JTable itemTable;
    private final DefaultTableModel tableModel;
    private static final String[] TABLE_COLUMNS = {
        "Serial #", "Name", "Description", "Quantity", "Supplier ID", "Expiry Date"
    };

    // Add Supplier Fields
    private final JTextField tfSupplierID;
    private final JTextField tfSupplierName;
    private final JTextField tfSupplierEmail;
    private final JTextField tfSupplierPhone;
    private final JTextField tfSupplierAddress;

    private static final int FORMS_HEIGHT = 280;  // Reduced from 320
    private static final int TABLE_HEIGHT = 200;  // Reduced from 250
    private static final int OUTPUT_HEIGHT = 150; // Reduced from 200
    private JLabel clockLabel;  // Add clock label
    private Timer clockTimer;   // Add timer for clock updates

    public InventoryUI() {
        super(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Set background color
        setBackground(BACKGROUND_COLOR);
        
        // Initialize components
        manager = new InventoryManager();
        XMLParser parser = new XMLParser();
        SupirXMLImporter importer = new SupirXMLImporter(parser, manager);

        // Initialize clock
        setupClock();

        // Initialize table
        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemTable = new JTable(tableModel);
        setupTable();
        
        // Initialize text fields
        tfSerialNum = createTextField();
        tfItemName = createTextField();
        tfDescription = createTextField();
        tfQuantity = createTextField();
        tfSupplierId = createTextField();
        tfExpDate = createTextField();
        tfUpdateSerial = createTextField();
        tfUpdateQuantity = createTextField();
        tfUpdateSupplierId = createTextField();
        
        // Initialize supplier fields
        tfSupplierID = createTextField();
        tfSupplierName = createTextField();
        tfSupplierEmail = createTextField();
        tfSupplierPhone = createTextField();
        tfSupplierAddress = createTextField();
        
        // Initialize output area
        outputArea = createOutputArea();
        
        // Create main layout
        setupMainLayout(importer);
        
        // Set window properties
        setMinimumSize(new Dimension(900, 750));  // Reduced from 950, 850
        setPreferredSize(new Dimension(900, 750));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Load initial data
        refreshItemTable();
    }
    
    private void setupTable() {
        itemTable.setFillsViewportHeight(true);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.getTableHeader().setReorderingAllowed(false);
        itemTable.setRowHeight(20);  // Reduced from 22
        
        // Adjust column widths for better visibility
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // Serial
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(140);  // Name
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(140);  // Description
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(60);   // Quantity
        itemTable.getColumnModel().getColumn(4).setPreferredWidth(160);  // Supplier
        itemTable.getColumnModel().getColumn(5).setPreferredWidth(90);   // Expiry
        
        // Add selection listener
        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && itemTable.getSelectedRow() != -1) {
                int row = itemTable.getSelectedRow();
                populateUpdateFields(row);
            }
        });
        
        // Set table properties for better appearance
        itemTable.setShowGrid(true);
        itemTable.setGridColor(new Color(230, 230, 230));
        itemTable.setIntercellSpacing(new Dimension(1, 1));
    }
    
    private void populateUpdateFields(int row) {
        tfUpdateSerial.setText(itemTable.getValueAt(row, 0).toString());
        tfUpdateQuantity.setText(itemTable.getValueAt(row, 3).toString());
        if (itemTable.getValueAt(row, 4) != null) {
            tfUpdateSupplierId.setText(itemTable.getValueAt(row, 4).toString());
        }
    }
    
    private void refreshItemTable() {
        tableModel.setRowCount(0);
        List<Item> items = AccessLoader.loadItems();
        Map<String, Supplier> suppliers = AccessLoader.loadSuppliers();
        
        for (Item item : items) {
            String supplierId = item.getSupplier() != null ? item.getSupplier().getId() : "";
            String supplierName = "";
            if (supplierId != null && !supplierId.isEmpty()) {
                Supplier supplier = suppliers.get(supplierId);
                if (supplier != null) {
                    supplierName = supplier.getName();
                }
            }
            
            tableModel.addRow(new Object[]{
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getQuantity(),
                supplierId + (supplierName.isEmpty() ? "" : " - " + supplierName),
                item.getExpiryDate() != null ? item.getExpiryDate().toString() : ""
            });
        }
        
        // Update column headers to be more descriptive
        String[] enhancedColumns = {
            "Serial #", "Item Name", "Description", "Stock Qty", "Supplier (ID - Name)", "Expiry Date"
        };
        for (int i = 0; i < enhancedColumns.length; i++) {
            itemTable.getColumnModel().getColumn(i).setHeaderValue(enhancedColumns[i]);
        }
        itemTable.getTableHeader().repaint();
    }
    
    private void setupClock() {
        clockLabel = new JLabel();
        clockLabel.setFont(HEADER_FONT);
        clockLabel.setForeground(new Color(80, 80, 80));
        updateClock(); // Initial update

        // Create timer for clock updates
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
    }

    private void updateClock() {
        String time = String.format("%tT", System.currentTimeMillis());
        clockLabel.setText(time);
    }

    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }
    
    private void setupMainLayout(SupirXMLImporter importer) {
        JPanel mainPanel = new JPanel(new BorderLayout(PADDING, PADDING));
        mainPanel.setBorder(new EmptyBorder(FORM_PADDING, FORM_PADDING, FORM_PADDING, FORM_PADDING));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOR);
        
        // Create inventory tab with BoxLayout for vertical stacking
        JPanel inventoryTab = new JPanel();
        inventoryTab.setLayout(new BoxLayout(inventoryTab, BoxLayout.Y_AXIS));
        inventoryTab.setBackground(BACKGROUND_COLOR);
        
        // Create top panel with forms and actions
        JPanel topPanel = new JPanel(new BorderLayout(PADDING, PADDING));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, FORMS_HEIGHT));
        
        // Create forms panel with fixed height
        JPanel formsPanel = new JPanel(new GridLayout(2, 1, PADDING, PADDING));
        formsPanel.setBackground(BACKGROUND_COLOR);
        formsPanel.setPreferredSize(new Dimension(0, FORMS_HEIGHT));
        formsPanel.add(createDataEntryPanel());
        formsPanel.add(createUpdatePanel());
        
        // Create action panel
        JPanel actionPanel = createActionPanel(importer);
        
        topPanel.add(formsPanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        // Create table panel with fixed height
        JPanel tablePanel = createTablePanel();
        tablePanel.setPreferredSize(new Dimension(0, TABLE_HEIGHT));
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, TABLE_HEIGHT));
        
        // Add components to inventory tab
        inventoryTab.add(topPanel);
        inventoryTab.add(Box.createRigidArea(new Dimension(0, PADDING)));
        inventoryTab.add(tablePanel);
        
        // Create supplier tab
        JPanel supplierTab = createSupplierPanel();
        
        // Add tabs
        tabbedPane.addTab("Inventory", new JScrollPane(inventoryTab));
        tabbedPane.addTab("Suppliers", supplierTab);
        
        // Add components to main panel
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(createOutputPanel(), BorderLayout.SOUTH);
        
        // Add to frame
        setLayout(new BorderLayout());
        add(mainPanel);
    }
    
    private JPanel createDataEntryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createStyledTitledBorder("Add New Item"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        
        addLabelAndField(panel, "Serial Number:", tfSerialNum, gbc, 0);
        addLabelAndField(panel, "Item Name:", tfItemName, gbc, 1);
        addLabelAndField(panel, "Description:", tfDescription, gbc, 2);
        addLabelAndField(panel, "Quantity:", tfQuantity, gbc, 3);
        addLabelAndField(panel, "Supplier ID:", tfSupplierId, gbc, 4);
        addLabelAndField(panel, "Exp. Date (YYYY-MM-DD):", tfExpDate, gbc, 5);
        
        // Add submit button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 4, 4, 4);
        JButton submitButton = createStyledButton("Add Item", e -> handleAddItem());
        panel.add(submitButton, gbc);
        
        return panel;
    }
    
    private JPanel createUpdatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createStyledTitledBorder("Update Item"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        
        addLabelAndField(panel, "Serial Number:", tfUpdateSerial, gbc, 0);
        addLabelAndField(panel, "New Quantity:", tfUpdateQuantity, gbc, 1);
        addLabelAndField(panel, "New Supplier ID:", tfUpdateSupplierId, gbc, 2);
        
        // Add update button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 4, 4, 4);
        JButton updateButton = createStyledButton("Update Item", e -> handleUpdateItem());
        panel.add(updateButton, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createStyledTitledBorder("Inventory Items"));
        
        JScrollPane scrollPane = new JScrollPane(itemTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = createStyledButton("Refresh", e -> refreshItemTable());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createActionPanel(SupirXMLImporter importer) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(createStyledTitledBorder("Actions"));
        panel.setPreferredSize(new Dimension(160, 0)); // Reduced from 180
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4); // Reduced padding
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        
        // Data Import Section
        JPanel importPanel = new JPanel(new GridLayout(0, 1, 4, 4)); // Reduced spacing
        importPanel.setBackground(BACKGROUND_COLOR);
        importPanel.setBorder(createGroupBorder("Data Management"));
        importPanel.add(createStyledButton("\ud83d\udce5 Import", e -> {
            handleImportXML(importer);
            refreshItemTable();
        }));
        gbc.gridy = 0;
        panel.add(importPanel, gbc);
        
        // Inventory Management Section
        JPanel inventoryPanel = new JPanel(new GridLayout(0, 1, 4, 4)); // Reduced spacing
        inventoryPanel.setBackground(BACKGROUND_COLOR);
        inventoryPanel.setBorder(createGroupBorder("Inventory"));
        inventoryPanel.add(createStyledButton("\u26a0 Alerts", e -> handleShowAlerts()));
        inventoryPanel.add(createStyledButton("\ud83d\udccb Suppliers", e -> handleShowSuppliers()));
        inventoryPanel.add(createStyledButton("\ud83d\udcd1 Items", e -> handleShowItems()));
        gbc.gridy = 1;
        panel.add(inventoryPanel, gbc);
        
        // Add filler
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBorder(createStyledTitledBorder("Output Console"));
        panel.setPreferredSize(new Dimension(0, OUTPUT_HEIGHT));
        
        // Create a header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6)); // Reduced padding
        
        // Add timestamp and clock to header
        String timestamp = String.format("Session: %tA, %<tB %<td, %<tY", System.currentTimeMillis());
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); // Reduced spacing
        timePanel.setBackground(HEADER_BACKGROUND);
        
        JLabel timestampLabel = new JLabel(timestamp);
        timestampLabel.setFont(HEADER_FONT);
        timestampLabel.setForeground(new Color(100, 100, 100));
        
        timePanel.add(timestampLabel);
        timePanel.add(new JLabel("|"));
        timePanel.add(clockLabel);
        
        headerPanel.add(timePanel, BorderLayout.WEST);
        
        // Create scroll pane with custom styling
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(OUTPUT_BACKGROUND);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4)); // Reduced padding
        buttonPanel.setBackground(HEADER_BACKGROUND);
        
        // Enhanced clear button
        JButton clearButton = createStyledButton("Clear", e -> {
            outputArea.setText("");
            String clearTime = String.format("Console cleared on %tA, %<tB %<td, %<tY at %<tT%n%n", 
                System.currentTimeMillis());
            outputArea.append(clearTime);
        });
        clearButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clearButton.setPreferredSize(new Dimension(80, 22)); // Smaller clear button
        buttonPanel.add(clearButton);
        
        // Assemble the panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.PLAIN));
        button.setPreferredSize(BUTTON_SIZE);
        return button;
    }
    
    private TitledBorder createStyledTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            title,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            null,
            Color.DARK_GRAY
        );
    }
    
    private Border createGroupBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR.brighter()),
            title,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            null,
            Color.GRAY
        );
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(TEXT_FIELD_SIZE);
        field.setMaximumSize(TEXT_FIELD_SIZE_DIM);
        field.setPreferredSize(TEXT_FIELD_SIZE_DIM);
        return field;
    }
    
    private JTextArea createOutputArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(OUTPUT_FONT);
        area.setMargin(new Insets(8, 8, 8, 8));  // Increased padding
        area.setBackground(OUTPUT_BACKGROUND);
        area.setForeground(new Color(50, 50, 50));  // Darker text for better contrast
        return area;
    }
    
    private void addLabelAndField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
    
    // Action Handlers
    private void handleImportXML(SupirXMLImporter importer) {
            try {
                importer.importFromSupir("src/supir_items.xml");
                showMessage("\u2705 XML import completed.");
            } catch (Exception ex) {
            showError("Failed to import XML", ex);
        }
            }

    private void handleShowAlerts() {
        try {
            List<Item> alerts = manager.generateAlerts();
            outputArea.setText("");
            String timestamp = String.format("Report generated on %tA, %<tB %<td, %<tY at %<tT%n%n", 
                System.currentTimeMillis());
            output(timestamp);
            
            if (alerts.isEmpty()) {
                output("\n  === Inventory Alerts ===\n\n");
                output("  ✓ No low-stock items found.\n\n");
            } else {
                output("\n  === Inventory Alerts ===\n\n");
                output("  ⚠ Low Stock Items (Threshold: 10)\n");
                output("  " + "─".repeat(100) + "\n");
                output(String.format("  %-8s %-25s %-20s %-8s %-25s\n",
                    "Serial#", "Name", "Description", "Stock", "Supplier"));
                output("  " + "─".repeat(100) + "\n");
                
                for (Item item : alerts) {
                    String supplierInfo = item.getSupplier() != null ? 
                        item.getSupplier().getId() + " - " + item.getSupplier().getName() : "Not Assigned";
                    
                    output(String.format("  %-8d %-25s %-20s %-8d %-25s\n",
                        item.getId(),
                        truncateString(item.getName(), 24),
                        truncateString(item.getDescription(), 19),
                        item.getQuantity(),
                        truncateString(supplierInfo, 24)
                    ));
                }
                output("  " + "─".repeat(100) + "\n");
                output(String.format("\n  Total low stock items: %d\n", alerts.size()));
                output("\n  Note: Items with stock below 10 units are flagged for reordering.\n\n");
            }
        } catch (RuntimeException e) {
            handleDatabaseError(e);
        }
    }
    
    private void handleDatabaseError(Exception e) {
        String errorMessage = e.getMessage();
        if (errorMessage.contains("database") || errorMessage.contains("Database")) {
            showError("Database Error", new Exception(
                "Cannot connect to the database. Please ensure:\n" +
                "1. The database file 'database2016b.accdb' exists in the project root or 'database' folder\n" +
                "2. You have read/write permissions for the database file\n" +
                "3. The database file is not corrupted\n\n" +
                "Technical details: " + e.getMessage()
            ));
        } else {
            showError("Operation Failed", e);
        }
    }
    
    private void handleAddItem() {
        try {
            validateAddItemFields();
            
            int serial = Integer.parseInt(tfSerialNum.getText().trim());
            String name = tfItemName.getText().trim();
            String desc = tfDescription.getText().trim();
            int qty = Integer.parseInt(tfQuantity.getText().trim());
            int supplierId = Integer.parseInt(tfSupplierId.getText().trim());
            LocalDate exp = LocalDate.parse(tfExpDate.getText().trim());
            
            Supplier supplier = new Supplier(String.valueOf(supplierId), "Manual", "", "", "");
            manager.addSupplier(supplier);
            
            Item item = new Item(serial, name, desc, null, qty, exp, supplier);
            manager.addItem(item);
            
            showMessage("\u2705 Item added: " + item.getName());
            clearAddItemFields();
            refreshItemTable();
        } catch (ValidationException ex) {
            showError("Validation Error", ex);
        } catch (RuntimeException e) {
            handleDatabaseError(e);
        }
    }

    private void handleUpdateItem() {
        try {
            validateUpdateFields();
            
            int serial = Integer.parseInt(tfUpdateSerial.getText().trim());

            if (!tfUpdateQuantity.getText().trim().isEmpty()) {
                int qty = Integer.parseInt(tfUpdateQuantity.getText().trim());
                manager.updateStock(serial, qty);
            }

            if (!tfUpdateSupplierId.getText().trim().isEmpty()) {
                int sid = Integer.parseInt(tfUpdateSupplierId.getText().trim());
                Supplier supplier = new Supplier(String.valueOf(sid), "Manual", "", "", "");
                manager.addSupplier(supplier);
                manager.assignSupplier(serial, supplier);
            }

            showMessage("\u2705 Update complete for item " + serial);
            clearUpdateFields();
            refreshItemTable();
        } catch (ValidationException ex) {
            showError("Validation Error", ex);
        } catch (RuntimeException e) {
            handleDatabaseError(e);
        }
    }

    private void handleShowSuppliers() {
        List<Supplier> suppliers = AccessLoader.loadSuppliersAsList();
        outputArea.setText("");
        String timestamp = String.format("Report generated on %tA, %<tB %<td, %<tY at %<tT%n%n", 
            System.currentTimeMillis());
        output(timestamp);
        
        if (suppliers.isEmpty()) {
            showMessage("No suppliers found.");
        } else {
            output("\n  === Current Suppliers ===\n\n");
            output(String.format("  %-8s %-20s %-25s %-15s %-30s\n", 
                "ID", "Name", "Email", "Phone", "Address"));
            output("  " + "─".repeat(100) + "\n");
            
            for (Supplier s : suppliers) {
                output(String.format("  %-8s %-20s %-25s %-15s %-30s\n",
                    s.getId(),
                    truncateString(s.getName(), 19),
                    truncateString(s.getEmail(), 24),
                    truncateString(s.getContactNumber(), 14),
                    truncateString(s.getAddress(), 29)
                ));
            }
            output("  " + "─".repeat(100) + "\n");
            output(String.format("\n  Total Suppliers: %d\n\n", suppliers.size()));
        }
    }
    
    private String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 2) + ".." : str;
    }
    
    // Validation Methods
    private void validateAddItemFields() throws ValidationException {
        if (tfSerialNum.getText().trim().isEmpty()) throw new ValidationException("Serial number is required");
        if (tfItemName.getText().trim().isEmpty()) throw new ValidationException("Item name is required");
        if (tfQuantity.getText().trim().isEmpty()) throw new ValidationException("Quantity is required");
        if (tfSupplierId.getText().trim().isEmpty()) throw new ValidationException("Supplier ID is required");
        if (tfExpDate.getText().trim().isEmpty()) throw new ValidationException("Expiry date is required");
        
        try {
            Integer.parseInt(tfSerialNum.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Serial number must be a number");
        }
        
        try {
            int qty = Integer.parseInt(tfQuantity.getText().trim());
            if (qty < 0) throw new ValidationException("Quantity cannot be negative");
        } catch (NumberFormatException e) {
            throw new ValidationException("Quantity must be a number");
        }
        
        try {
            Integer.parseInt(tfSupplierId.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Supplier ID must be a number");
        }
        
        try {
            LocalDate.parse(tfExpDate.getText().trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Use YYYY-MM-DD");
        }
    }
    
    private void validateUpdateFields() throws ValidationException {
        if (tfUpdateSerial.getText().trim().isEmpty()) {
            throw new ValidationException("Serial number is required for update");
        }
        
        try {
            Integer.parseInt(tfUpdateSerial.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Serial number must be a number");
        }
        
        if (!tfUpdateQuantity.getText().trim().isEmpty()) {
            try {
                int qty = Integer.parseInt(tfUpdateQuantity.getText().trim());
                if (qty < 0) throw new ValidationException("Quantity cannot be negative");
            } catch (NumberFormatException e) {
                throw new ValidationException("Quantity must be a number");
            }
        }
        
        if (!tfUpdateSupplierId.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(tfUpdateSupplierId.getText().trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("Supplier ID must be a number");
            }
        }
    }
    
    // Utility Methods
    private void clearAddItemFields() {
        tfSerialNum.setText("");
        tfItemName.setText("");
        tfDescription.setText("");
        tfQuantity.setText("");
        tfSupplierId.setText("");
        tfExpDate.setText("");
    }
    
    private void clearUpdateFields() {
        tfUpdateSerial.setText("");
        tfUpdateQuantity.setText("");
        tfUpdateSupplierId.setText("");
    }

    private void output(String text) {
        outputArea.append(text);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, 
            ex.getMessage(), 
            "\u274c " + title,
            JOptionPane.ERROR_MESSAGE);
    }
    
    private static class ValidationException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public ValidationException(String message) {
            super(message);
        }
    }

    private JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout(PADDING, PADDING));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(FORM_PADDING, FORM_PADDING, FORM_PADDING, FORM_PADDING));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(createStyledTitledBorder("Add New Supplier"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Add form fields
        addLabelAndField(formPanel, "Supplier ID:", tfSupplierID, gbc, 0);
        addLabelAndField(formPanel, "Name:", tfSupplierName, gbc, 1);
        addLabelAndField(formPanel, "Email:", tfSupplierEmail, gbc, 2);
        addLabelAndField(formPanel, "Phone:", tfSupplierPhone, gbc, 3);
        addLabelAndField(formPanel, "Address:", tfSupplierAddress, gbc, 4);

        // Add submit button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 4, 4, 4);
        JButton submitButton = createStyledButton("Add Supplier", e -> handleAddSupplier());
        formPanel.add(submitButton, gbc);

        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    private void handleAddSupplier() {
        try {
            validateSupplierFields();
            
            String id = tfSupplierID.getText().trim();
            String name = tfSupplierName.getText().trim();
            String email = tfSupplierEmail.getText().trim();
            String phone = tfSupplierPhone.getText().trim();
            String address = tfSupplierAddress.getText().trim();
            
            Supplier supplier = new Supplier(id, name, phone, email, address);
            manager.addSupplier(supplier);
            
            showMessage("\u2705 Supplier added successfully: " + name);
            clearSupplierFields();
            
        } catch (ValidationException ex) {
            showError("Validation Error", ex);
        } catch (Exception ex) {
            showError("Error adding supplier", ex);
        }
    }

    private void validateSupplierFields() throws ValidationException {
        if (tfSupplierID.getText().trim().isEmpty()) throw new ValidationException("Supplier ID is required");
        if (tfSupplierName.getText().trim().isEmpty()) throw new ValidationException("Supplier name is required");
        if (tfSupplierEmail.getText().trim().isEmpty()) throw new ValidationException("Email is required");
        if (tfSupplierPhone.getText().trim().isEmpty()) throw new ValidationException("Phone number is required");
        
        try {
            Integer.parseInt(tfSupplierID.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Supplier ID must be a number");
        }
        
        // Basic email validation
        String email = tfSupplierEmail.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            throw new ValidationException("Invalid email format");
        }
    }

    private void clearSupplierFields() {
        tfSupplierID.setText("");
        tfSupplierName.setText("");
        tfSupplierEmail.setText("");
        tfSupplierPhone.setText("");
        tfSupplierAddress.setText("");
    }

    private void handleShowItems() {
        List<Item> items = AccessLoader.loadItems();
        Map<String, Supplier> suppliers = AccessLoader.loadSuppliers();
        
        outputArea.setText("");
        String timestamp = String.format("Report generated on %tA, %<tB %<td, %<tY at %<tT%n%n", 
            System.currentTimeMillis());
        output(timestamp);
        
        if (items.isEmpty()) {
            showMessage("No items found in inventory.");
        } else {
            output("\n  === Current Inventory Items ===\n\n");
            output(String.format("  %-8s %-25s %-20s %-8s %-25s %-12s\n",
                "Serial#", "Name", "Description", "Stock", "Supplier", "Expiry Date"));
            output("  " + "─".repeat(100) + "\n");
            
            for (Item item : items) {
                String supplierInfo = "";
                if (item.getSupplier() != null) {
                    String suppId = item.getSupplier().getId();
                    Supplier supplier = suppliers.get(suppId);
                    if (supplier != null) {
                        supplierInfo = suppId + " - " + supplier.getName();
                    } else {
                        supplierInfo = suppId;
                    }
                }
                
                output(String.format("  %-8d %-25s %-20s %-8d %-25s %-12s\n",
                    item.getId(),
                    truncateString(item.getName(), 24),
                    truncateString(item.getDescription(), 19),
                    item.getQuantity(),
                    truncateString(supplierInfo, 24),
                    item.getExpiryDate() != null ? item.getExpiryDate().toString() : "N/A"
                ));
            }
            
            output("  " + "─".repeat(100) + "\n");
            output(String.format("\n  Total Items: %d\n", items.size()));
            
            // Show low stock items
            List<Item> lowStockItems = items.stream()
                .filter(item -> item.getQuantity() < 10)
                .toList();
            
            if (!lowStockItems.isEmpty()) {
                output("\n  ⚠ Low Stock Items (< 10):\n");
                for (Item item : lowStockItems) {
                    output(String.format("  • %s (ID: %d) - Current Stock: %d\n",
                        item.getName(), item.getId(), item.getQuantity()));
                }
            }
            output("\n");
        }
    }
}

