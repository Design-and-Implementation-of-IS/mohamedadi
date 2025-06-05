package control;

import entity.Item;
import entity.Supplier;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class InventoryManager {
    private static final String DB_PATH = "jdbc:ucanaccess://C:/Users/mhema/DentalCareData.accdb";
    private final InventoryAlertGenerator alertGenerator;

    public InventoryManager() {
        this.alertGenerator = new InventoryAlertGenerator();
        validateDatabaseConnection();
    }

    private void validateDatabaseConnection() {
        File dbFile = new File("C:/Users/mhema/DentalCareData.accdb");
        if (!dbFile.exists()) {
            throw new RuntimeException("Database file not found at: " + dbFile.getAbsolutePath() + 
                "\nPlease ensure the database file exists and you have proper permissions.");
        }
        
        // Test the connection
        try (Connection conn = getConnection()) {
            // Connection successful
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database. Error: " + e.getMessage(), e);
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_PATH);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database at " + DB_PATH + ". Error: " + e.getMessage(), e);
        }
    }

    public void addSupplier(Supplier supplier) {
        try (Connection conn = getConnection()) {
            // Check if supplier already exists
            String checkSql = "SELECT COUNT(*) FROM TblSuppliers WHERE supplierId = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, Integer.parseInt(supplier.getId()));
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();

            if (count == 0) {
                String sql = "INSERT INTO TblSuppliers (supplierId, supplierName, email, phoneNum, address) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(supplier.getId()));
                stmt.setString(2, supplier.getName());
                stmt.setString(3, supplier.getEmail());
                stmt.setString(4, supplier.getContactNumber());
                stmt.setString(5, supplier.getAddress());
                stmt.executeUpdate();
                stmt.close();
            } else {
                System.out.println("Supplier already exists with ID " + supplier.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add supplier: " + e.getMessage(), e);
        }
    }

    public void addItem(Item item) {
        try (Connection conn = getConnection()) {
            // Check if item already exists
            String checkSql = "SELECT COUNT(*) FROM TblInventoryItems WHERE serialNum = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, item.getId());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();

            if (count == 0) {
                String sql = "INSERT INTO TblInventoryItems (serialNum, itemName, description, quantityInStock, supplierId, expDate) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, item.getId());
                stmt.setString(2, item.getName());
                stmt.setString(3, item.getDescription());
                stmt.setInt(4, item.getQuantity());

                if (item.getSupplier() != null) {
                    stmt.setInt(5, Integer.parseInt(item.getSupplier().getId()));
                } else {
                    stmt.setNull(5, Types.INTEGER);
                }

                if (item.getExpiryDate() != null) {
                    stmt.setDate(6, Date.valueOf(item.getExpiryDate()));
                } else {
                    stmt.setNull(6, Types.DATE);
                }

                stmt.executeUpdate();
                stmt.close();
            } else {
                System.out.println("❌ Item already exists with ID " + item.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add item: " + e.getMessage(), e);
        }
    }


    public void updateStock(int itemId, int newQuantity) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE TblInventoryItems SET quantityInStock = ? WHERE serialNum = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, itemId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                System.out.println("❌ Item not found: " + itemId);
            }
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update stock: " + e.getMessage(), e);
        }
    }

    public void assignSupplier(int itemId, Supplier supplier) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE TblInventoryItems SET supplierId = ? WHERE serialNum = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(supplier.getId()));
            stmt.setInt(2, itemId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                System.out.println("Item not found.");
            }
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign supplier: " + e.getMessage(), e);
        }
    }

    public List<Item> generateAlerts() {
        List<Item> items = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT i.*, s.supplierName FROM TblInventoryItems i LEFT JOIN TblSuppliers s ON i.supplierId = s.supplierId";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("serialNum");
                String name = rs.getString("itemName");
                String description = rs.getString("description");
                int quantity = rs.getInt("quantityInStock");
                Date sqlDate = rs.getDate("expDate");
                LocalDate expiryDate = sqlDate != null ? sqlDate.toLocalDate() : null;
                
                // Get supplier info if available
                int supplierId = rs.getInt("supplierId");
                String supplierName = rs.getString("supplierName");
                Supplier supplier = null;
                if (!rs.wasNull() && supplierName != null) {
                    supplier = new Supplier(String.valueOf(supplierId), supplierName, "", "", "");
                }

                Item item = new Item(id, name, description, null, quantity, expiryDate, supplier);
                items.add(item);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate alerts: " + e.getMessage(), e);
        }
        
        // Use the alert generator to check for low stock items
        return alertGenerator.checkLowStock(items, 10);
    }
}

